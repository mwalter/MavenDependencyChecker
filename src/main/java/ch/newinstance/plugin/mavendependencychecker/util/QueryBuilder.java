package ch.newinstance.plugin.mavendependencychecker.util;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder {

    private static final String GROUP_VAR = "g:";
    private static final String ARTIFACT_VAR = "a:";
    private static final String AND = "+AND+";
    private static final String QUOTATION = "%20";

    public List<String> buildDependencyQueries(List<Dependency> dependencies) {
        return dependencies.stream().map(dependency -> buildQuery(dependency.getGroupId(), dependency.getArtifactId())).collect(Collectors.toList());
    }

    public List<String> buildPluginQueries(List<Plugin> plugins) {
        return plugins.stream().map(plugin -> buildQuery(plugin.getGroupId(), plugin.getArtifactId())).collect(Collectors.toList());
    }

    private String buildQuery(String groupId, String artifactId) {
        return GROUP_VAR + QUOTATION + groupId + QUOTATION + AND + ARTIFACT_VAR + QUOTATION + artifactId + QUOTATION;
    }

}