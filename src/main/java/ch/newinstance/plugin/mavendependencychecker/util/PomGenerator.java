package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class PomGenerator {

    private final MavenXpp3Writer writer = new MavenXpp3Writer();

    private boolean existsBuildSection;

    public String generatePom(List<DependencyUpdateResult> dependenciesToUpdate) {
        Model model = new Model();
        model.setDescription("GENERATED FOR COPYING PURPOSE ONLY. DOES NOT REPLACE YOUR POM INTENTIONALLY.");

        for (DependencyUpdateResult dependencyUpdateResult : dependenciesToUpdate) {
            if (dependencyUpdateResult.isDependency()) {
                model.addDependency(createDependency(dependencyUpdateResult));
            } else {
                if (!existsBuildSection) {
                    model.setBuild(new Build());
                    existsBuildSection = true;
                }
                model.getBuild().getPlugins().add(createPlugin(dependencyUpdateResult));
            }
        }

        StringWriter stringWriter = new StringWriter();
        try {
            writer.write(stringWriter, model);
        } catch (IOException ioe) {
            return StringUtils.EMPTY;
        }

        return stringWriter.toString();
    }

    private Dependency createDependency(DependencyUpdateResult dependencyUpdateResult) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(dependencyUpdateResult.getGroupId());
        dependency.setArtifactId(dependencyUpdateResult.getArtifactId());
        dependency.setVersion(dependencyUpdateResult.getLatestVersion());
        return dependency;
    }

    private Plugin createPlugin(DependencyUpdateResult dependencyUpdateResult) {
        Plugin plugin = new Plugin();
        plugin.setGroupId(dependencyUpdateResult.getGroupId());
        plugin.setArtifactId(dependencyUpdateResult.getArtifactId());
        plugin.setVersion(dependencyUpdateResult.getLatestVersion());
        return plugin;
    }

}
