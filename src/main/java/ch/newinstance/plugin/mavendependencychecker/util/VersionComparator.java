package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.model.Dependency;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VersionComparator {

    private static final String RESPONSE = "response";
    private static final String DOCS_ARRAY = "docs";
    private static final String VERSION = "v";
    private static final String GROUP = "g";
    private static final String ARTIFACT = "a";

    private final Map<String, String> moduleDependencies;

    private final List<Dependency> mavenDependencies;

    public VersionComparator(Map<String, String> moduleDependencies, List<Dependency> mavenDependencies) {
        this.moduleDependencies = moduleDependencies;
        this.mavenDependencies = mavenDependencies;
    }

    public List<DependencyUpdateResult> compareVersions(List<String> jsons) {
        List<DependencyUpdateResult> result = new ArrayList<>();
        for (String json : jsons) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject response = jsonObject.getJSONObject(RESPONSE);
                JSONObject docsObject = response.getJSONArray(DOCS_ARRAY).getJSONObject(0);
                String groupId = docsObject.getString(GROUP);
                String artifactId = docsObject.getString(ARTIFACT);
                String latestVersion = docsObject.getString(VERSION);
                ComparableVersion latestVersionComparable = new ComparableVersion(latestVersion);

                String key = groupId + ":" + artifactId;

                // if not managed by IntelliJ use current POM version instead
                String currentVersion = moduleDependencies.get(key) != null ? moduleDependencies.get(key) : getCurrentMavenDependencyVersion(groupId, artifactId);

                if (currentVersion == null) {
                    // if still null skip version comparison
                    continue;
                }

                ComparableVersion currentVersionComparable = new ComparableVersion(currentVersion);

                if (latestVersionComparable.compareTo(currentVersionComparable) > 0) {
                    result.add(new DependencyUpdateResult(groupId, artifactId, currentVersion, latestVersion));
                }
            } catch (JSONException je) {
                // ignoring
            }
        }
        return result;
    }

    private String getCurrentMavenDependencyVersion(String groupId, String artifactId) {
        for (Dependency mavenDependency : mavenDependencies) {
            if (mavenDependency.getGroupId().equals(groupId) && mavenDependency.getArtifactId().equals(artifactId)) {
                return mavenDependency.getVersion();
            }
        }
        return null;
    }

}
