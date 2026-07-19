package ch.newinstance.plugin.mavendependencychecker.model;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenPlugin;

import java.util.List;

public record DependencyParseResult(List<Dependency> dependencies, List<Plugin> plugins,
                                    List<MavenArtifact> moduleDependencies, List<MavenPlugin> projectPlugins) {
}

