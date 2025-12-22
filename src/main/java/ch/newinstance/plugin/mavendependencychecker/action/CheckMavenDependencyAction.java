package ch.newinstance.plugin.mavendependencychecker.action;

import ch.newinstance.plugin.mavendependencychecker.client.MavenSearchClient;
import ch.newinstance.plugin.mavendependencychecker.config.MavenDependencyCheckerSettings;
import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import ch.newinstance.plugin.mavendependencychecker.parser.DependencyParser;
import ch.newinstance.plugin.mavendependencychecker.ui.ResultDialog;
import ch.newinstance.plugin.mavendependencychecker.util.MessageCreator;
import ch.newinstance.plugin.mavendependencychecker.util.PomGenerator;
import ch.newinstance.plugin.mavendependencychecker.util.QueryBuilder;
import ch.newinstance.plugin.mavendependencychecker.util.VersionComparator;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.ui.UIUtil;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenPlugin;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class CheckMavenDependencyAction extends AnAction {

    private static final String POM_FILE = "pom.xml";

    private static final String[] CANCEL_OPTIONS = {"Got it", "Never mind", "So what?", "Don't tell security!", "Don't panic", "Keep calm and update"};
    private static final String MSG_CONSIDER_UPGRADING = "You should consider upgrading the following project dependencies:\n\n";
    private static final String MSG_OUTDATED_DEPENDENCIES = "Outdated Dependencies Found";
    private static final String MSG_MAJOR_VERSION_IGNORED = "+++ Major version updates of dependencies are ignored! +++\n\n";

    private final Random random = new SecureRandom();

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        boolean visible = psiFile != null && !psiFile.isDirectory() && psiFile.getName().equals(POM_FILE);
        event.getPresentation().setEnabledAndVisible(visible);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // read settings
        MavenDependencyCheckerSettings settings = ApplicationManager.getApplication().getService(MavenDependencyCheckerSettings.class);

        // read dependencies and plugins from POM file
        PsiFile pomFile = event.getData(CommonDataKeys.PSI_FILE);
        DependencyParser parser = new DependencyParser(pomFile);
        List<Dependency> mavenDependencies = parser.parseMavenDependencies();
        List<Plugin> plugins = parser.parseMavenPlugins();
        if (mavenDependencies.isEmpty() && plugins.isEmpty()) {
            Messages.showInfoMessage("No Maven dependencies or plugins found in POM file.\nNothing to check.", "No Maven Project Dependencies");
            return;
        }

        // read dependencies and plugin from IntelliJ Maven model
        List<MavenArtifact> moduleDependencies = parser.parseModuleDependencies();
        List<MavenPlugin> mavenPlugins = parser.parseProjectPlugins();
        if (moduleDependencies.isEmpty() && mavenPlugins.isEmpty()) {
            Messages.showInfoMessage("No project dependency information found.\nNothing to check.", "No Project Dependencies");
            return;
        }

        // fetch latest dependency and plugin versions from Maven Central
        QueryBuilder queryBuilder = new QueryBuilder();
        List<String> queries = queryBuilder.buildDependencyQueries(mavenDependencies);
        queries.addAll(queryBuilder.buildPluginQueries(plugins));
        MavenSearchClient searchClient = new MavenSearchClient();
        List<String> queryResults = searchClient.executeSearchQueries(queries);

        // compare current dependencies and plugins with latest version
        VersionComparator versionComparator = new VersionComparator(queryResults, settings);
        List<DependencyUpdateResult> dependenciesToUpdate = versionComparator.compareDependencyVersions(moduleDependencies, mavenDependencies);
        dependenciesToUpdate.addAll(versionComparator.comparePluginVersions(mavenPlugins, plugins));

        if (dependenciesToUpdate.isEmpty()) {
            Messages.showInfoMessage("All project dependencies use the latest version available.\nHappy coding!", "Everything Up To Date");
            return;
        }

        // show dependencies and plugins which can be updated
        showResultDialog(dependenciesToUpdate, event.getProject());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    private void showResultDialog(List<DependencyUpdateResult> dependenciesToUpdate, Project project) {
        String results = MessageCreator.createResultMessage(dependenciesToUpdate);
        int cancelOptionIndex = random.nextInt(CANCEL_OPTIONS.length);
        String[] options = {"Copy to Clipboard", "Open in Editor", CANCEL_OPTIONS[cancelOptionIndex]};

        int buttonPressed = showDialogAndGetUserInteraction(results, options);

        if (buttonPressed == 0) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(results), null);
        } else if (buttonPressed == 1) {
            openEditorTab(dependenciesToUpdate, project);
        }
    }

    private int showDialogAndGetUserInteraction(String results, String[] options) {
        MavenDependencyCheckerSettings settings = ApplicationManager.getApplication().getService(MavenDependencyCheckerSettings.class);

        String messageContent;
        if (settings.isMajorVersionChangeIgnored()) {
            messageContent = MSG_MAJOR_VERSION_IGNORED + MSG_CONSIDER_UPGRADING + results;
        } else {
            messageContent = MSG_CONSIDER_UPGRADING + results;
        }

        ResultDialog resultDialog = new ResultDialog(messageContent, MSG_OUTDATED_DEPENDENCIES, options, 0, UIUtil.getWarningIcon());
        resultDialog.show();

        return resultDialog.getExitCode();
    }

    private void openEditorTab(List<DependencyUpdateResult> dependenciesToUpdate, Project project) {
        PomGenerator pomGenerator = new PomGenerator();
        String pom = pomGenerator.generatePom(dependenciesToUpdate);
        PsiFile pomFile = PsiFileFactory.getInstance(project).createFileFromText("generatedPom.xml", XmlFileType.INSTANCE, pom);
        FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, pomFile.getViewProvider().getVirtualFile()), true);
    }

}
