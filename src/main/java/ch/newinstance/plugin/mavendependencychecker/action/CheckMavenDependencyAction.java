package ch.newinstance.plugin.mavendependencychecker.action;

import ch.newinstance.plugin.mavendependencychecker.client.MavenSearchClient;
import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import ch.newinstance.plugin.mavendependencychecker.util.MessageCreator;
import ch.newinstance.plugin.mavendependencychecker.util.QueryBuilder;
import ch.newinstance.plugin.mavendependencychecker.util.VersionComparator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckMavenDependencyAction extends AnAction {

    private static final String POM_FILE = "pom.xml";

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        String psiFileName = event.getData(CommonDataKeys.PSI_FILE).getName();
        event.getPresentation().setEnabledAndVisible(psiFileName.equals(POM_FILE));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        PsiFile pomFile = event.getData(CommonDataKeys.PSI_FILE);
        Map<String, String> moduleDependencies = retrieveModuleDependencies(pomFile);
        List<Dependency> dependencies = parseDependencies(pomFile.getText());

        if (dependencies.isEmpty()) {
            Messages.showInfoMessage("No project dependencies found in POM file.\nNothing to check.", "No Maven Project Dependencies");
            return;
        }

        QueryBuilder queryBuilder = new QueryBuilder();
        List<String> queries = queryBuilder.buildQueries(dependencies);

        MavenSearchClient searchClient = new MavenSearchClient();
        VersionComparator versionComparator = new VersionComparator(moduleDependencies);
        List<String> queryResults = searchClient.executeSearchQueries(queries);
        List<DependencyUpdateResult> dependenciesToUpdate = versionComparator.compareVersions(queryResults);

        if (dependenciesToUpdate.isEmpty()) {
            Messages.showInfoMessage("All project dependencies use the latest version available.\nHappy coding!", "Everything Up To Date");
            return;
        }

        String message = MessageCreator.createResultMessage(dependenciesToUpdate);
        Messages.showWarningDialog("You should consider upgrading the following project dependencies:\n" + message, "Outdated Dependencies");
    }

    private List<Dependency> parseDependencies(String xml) {
        if (StringUtils.isBlank(xml)) {
            return Collections.emptyList();
        }

        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            Model model = reader.read(new StringReader(xml));
            return model.getDependencies();
        } catch (IOException | XmlPullParserException ex) {
            return Collections.emptyList();
        }
    }

    private Map<String, String> retrieveModuleDependencies(PsiFile pomFile) {
        Module module = ProjectRootManager.getInstance(pomFile.getProject()).getFileIndex().getModuleForFile(pomFile.getVirtualFile());

        Map<String, String> libraryMap = new HashMap<>();
        ModuleRootManager.getInstance(module).orderEntries().forEachLibrary(library -> {
            String[] libraryParts = StringUtils.split(library.getName(), ":");
            libraryMap.put(libraryParts[1].trim() + ":" + libraryParts[2].trim(), libraryParts[3].trim());
            return true;
        });
        return libraryMap;
    }

}
