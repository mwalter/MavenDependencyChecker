package ch.newinstance.plugin.mavendependencychecker.action;

import ch.newinstance.plugin.mavendependencychecker.client.MavenSearchClient;
import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import ch.newinstance.plugin.mavendependencychecker.parser.DependencyParser;
import ch.newinstance.plugin.mavendependencychecker.ui.ResultDialog;
import ch.newinstance.plugin.mavendependencychecker.util.MessageCreator;
import ch.newinstance.plugin.mavendependencychecker.util.QueryBuilder;
import ch.newinstance.plugin.mavendependencychecker.util.VersionComparator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.UIUtil;
import org.apache.maven.model.Dependency;
import org.jetbrains.annotations.NotNull;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;

public class CheckMavenDependencyAction extends AnAction {

    private static final String POM_FILE = "pom.xml";

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        boolean visible = psiFile != null && !psiFile.isDirectory() && psiFile.getName().equals(POM_FILE);
        event.getPresentation().setEnabledAndVisible(visible);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiFile pomFile = event.getData(CommonDataKeys.PSI_FILE);
        DependencyParser parser = new DependencyParser(pomFile);
        List<Dependency> mavenDependencies = parser.parseMavenDependencies();

        if (mavenDependencies.isEmpty()) {
            Messages.showInfoMessage("No Maven dependencies found in POM file.\nNothing to check.", "No Maven Project Dependencies");
            return;
        }

        Map<String, String> moduleDependencies = parser.parseModuleDependencies();

        if (moduleDependencies.isEmpty()) {
            Messages.showInfoMessage("No project dependency information found.\nNothing to check.", "No Project Dependencies");
            return;
        }

        QueryBuilder queryBuilder = new QueryBuilder();
        List<String> queries = queryBuilder.buildQueries(mavenDependencies);

        MavenSearchClient searchClient = new MavenSearchClient();
        List<String> queryResults = searchClient.executeSearchQueries(queries);

        VersionComparator versionComparator = new VersionComparator(moduleDependencies, mavenDependencies);
        List<DependencyUpdateResult> dependenciesToUpdate = versionComparator.compareVersions(queryResults);

        if (dependenciesToUpdate.isEmpty()) {
            Messages.showInfoMessage("All project dependencies use the latest version available.\nHappy coding!", "Everything Up To Date");
            return;
        }

        showResultDialog(dependenciesToUpdate);
    }

    private void showResultDialog(List<DependencyUpdateResult> dependenciesToUpdate) {
        String message = MessageCreator.createResultMessage(dependenciesToUpdate);

        String[] options = {"Copy to Clipboard", "Got it"};
        ResultDialog resultDialog = new ResultDialog("You should consider upgrading the following project dependencies:\n\n" + message,
                "Outdated Dependencies Found", options, 0, UIUtil.getWarningIcon());
        resultDialog.show();
        int buttonPressed = resultDialog.getExitCode();

        if (buttonPressed == 0) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(message), null);
        }
    }

}
