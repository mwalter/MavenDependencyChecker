package ch.newinstance.plugin.mavendependencychecker.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class DependencyUpdate {
    
    private final String groupId;

    private final String artifactId;

    public DependencyUpdate(String groupId, String artifactId) {
        this.groupId = groupId;
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("groupId", groupId)
                .append("artifactId", artifactId)
                .toString();
    }
}
