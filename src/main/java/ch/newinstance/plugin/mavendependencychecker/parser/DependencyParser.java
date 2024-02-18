package ch.newinstance.plugin.mavendependencychecker.parser;

import com.intellij.psi.PsiFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class DependencyParser {

    private final PsiFile pomFile;

    private final MavenXpp3Reader reader = new MavenXpp3Reader();

    public DependencyParser(PsiFile pomFile) {
        this.pomFile = pomFile;
    }

    public List<Dependency> parseMavenDependencies() {
        if (StringUtils.isBlank(pomFile.getText())) {
            return Collections.emptyList();
        }

        try {
            Model model = reader.read(new StringReader(pomFile.getText()));
            List<Dependency> dependencies = new ArrayList<>(model.getDependencies());

            if (model.getDependencyManagement() != null) {
                dependencies.addAll(model.getDependencyManagement().getDependencies());
            }

            return parsePropertyPlaceholder(model, dependencies);
        } catch (IOException | XmlPullParserException ex) {
            return Collections.emptyList();
        }
    }

    public List<MavenArtifact> parseModuleDependencies() {
        List<MavenProject> mavenProjects = MavenProjectsManager.getInstance(pomFile.getProject()).getProjects();
        if (mavenProjects.size() > 1) {
            // handle multimodule project
            Optional<MavenProject> selectedProject = getMavenProject(mavenProjects);
            return selectedProject.map(MavenProject::getDependencies).orElse(Collections.emptyList());
        }
        return mavenProjects.get(0).getDependencies();
    }

    public List<Plugin> parseMavenPlugins() {
        if (StringUtils.isBlank(pomFile.getText())) {
            return Collections.emptyList();
        }

        List<Plugin> plugins = new ArrayList<>();

        try {
            Model model = reader.read(new StringReader(pomFile.getText()));

            if (model.getBuild() != null) {
                plugins.addAll(model.getBuild().getPlugins());
                if (model.getBuild().getPluginManagement() != null) {
                    plugins.addAll(model.getBuild().getPluginManagement().getPlugins());
                }
            }

        } catch (IOException | XmlPullParserException ex) {
            return Collections.emptyList();
        }

        return plugins;
    }

    public List<MavenPlugin> parseProjectPlugins() {
        List<MavenProject> mavenProjects = MavenProjectsManager.getInstance(pomFile.getProject()).getProjects();
        if (mavenProjects.size() > 1) {
            // handle multimodule project
            Optional<MavenProject> selectedProject = getMavenProject(mavenProjects);
            return selectedProject.map(MavenProject::getPlugins).orElse(Collections.emptyList());
        }
        return mavenProjects.get(0).getPlugins();
    }

    private List<Dependency> parsePropertyPlaceholder(Model model, List<Dependency> dependencies) {
        Properties properties = model.getProperties();
        if (properties.isEmpty()) {
            return dependencies;
        }

        for (Dependency dependency : dependencies) {
            String version = dependency.getVersion();
            if (version != null && version.startsWith("${")) {
                String propertyVersion = (String) properties.get(version.substring(2, version.length() - 1));
                dependency.setVersion(propertyVersion);
            }
            String groupId = dependency.getGroupId();
            if (groupId != null && groupId.startsWith("${")) {
                String propertyGroupId = (String) properties.get(groupId.substring(2, groupId.length() - 1));
                dependency.setGroupId(propertyGroupId);
            }
            String artifactId = dependency.getArtifactId();
            if (artifactId != null && artifactId.startsWith("${")) {
                String propertyArtifactId = (String) properties.get(artifactId.substring(2, artifactId.length() - 1));
                dependency.setArtifactId(propertyArtifactId);
            }
        }

        return dependencies;
    }

    private Optional<MavenProject> getMavenProject(List<MavenProject> mavenProjects) {
        String projectName = pomFile.getParent().getName(); // the POM file always has a parent
        for (MavenProject mavenProject : mavenProjects) {
            if (projectName.equals(mavenProject.getDisplayName())) {
                return Optional.of(mavenProject);
            }
        }
        return Optional.empty();
    }

}
