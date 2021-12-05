package ch.newinstance.plugin.mavendependencychecker.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class DependencyUpdateResult {

    private final String groupId;

    private final String artifactId;

    private final String currentVersion;

    private final String latestVersion;

    public DependencyUpdateResult(String groupId, String artifactId, String currentVersion, String latestVersion) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.currentVersion = currentVersion;
        this.latestVersion = latestVersion;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("groupId", groupId)
                .append("artifactId", artifactId)
                .append("currentVersion", currentVersion)
                .append("latestVersion", latestVersion)
                .toString();
    }
}
