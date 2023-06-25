package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VersionComparator {

    private static final String RESPONSE = "response";
    private static final String DOCS_ARRAY = "docs";
    private static final String VERSION = "v";
    private static final String GROUP = "g";
    private static final String ARTIFACT = "a";

    private final List<String> queryResults;

    private List<Dependency> mavenDependencies;

    private List<Plugin> mavenPlugins;

    public VersionComparator(List<String> queryResults) {
        this.queryResults = queryResults;
    }

    public List<DependencyUpdateResult> compareDependencyVersions(List<MavenArtifact> moduleDependencies, List<Dependency> mavenDependencies) {
        this.mavenDependencies = mavenDependencies;

        List<DependencyUpdateResult> result = new ArrayList<>();
        for (String json : queryResults) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject response = jsonObject.getJSONObject(RESPONSE);
                JSONObject docsObject = response.getJSONArray(DOCS_ARRAY).getJSONObject(0);
                String groupId = docsObject.getString(GROUP);
                String artifactId = docsObject.getString(ARTIFACT);
                String latestVersion = docsObject.getString(VERSION);
                ComparableVersion latestVersionComparable = new ComparableVersion(latestVersion);

                // if not managed by IntelliJ use current POM version instead
                String currentVersion = null;
                for (MavenArtifact mavenArtifact : moduleDependencies) {
                    if (mavenArtifact.getGroupId().equals(groupId) && mavenArtifact.getArtifactId().equals(artifactId)) {
                        currentVersion = mavenArtifact.getVersion() != null ? mavenArtifact.getVersion() : getCurrentMavenDependencyVersion(groupId, artifactId);
                    }
                }


                if (currentVersion == null) {
                    // if still null skip version comparison
                    continue;
                }

                ComparableVersion currentVersionComparable = new ComparableVersion(currentVersion);

                if (latestVersionComparable.compareTo(currentVersionComparable) > 0) {
                    result.add(new DependencyUpdateResult(groupId, artifactId, currentVersion, latestVersion));
                }
            } catch (JSONException je) {
                // ignore
            }
        }
        return result;
    }

    public List<DependencyUpdateResult> comparePluginVersions(List<MavenPlugin> projectPlugins, List<Plugin> mavenPlugins) {
        this.mavenPlugins = mavenPlugins;

        List<DependencyUpdateResult> result = new ArrayList<>();
        for (String json : queryResults) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject response = jsonObject.getJSONObject(RESPONSE);
                JSONObject docsObject = response.getJSONArray(DOCS_ARRAY).getJSONObject(0);
                String groupId = docsObject.getString(GROUP);
                String artifactId = docsObject.getString(ARTIFACT);
                String latestVersion = docsObject.getString(VERSION);
                ComparableVersion latestVersionComparable = new ComparableVersion(latestVersion);

                // if not managed by IntelliJ use current POM version instead
                String currentVersion = null;
                for (MavenPlugin projectPlugin : projectPlugins) {
                    if (projectPlugin.getGroupId().equals(groupId) && projectPlugin.getArtifactId().equals(artifactId)) {
                        currentVersion = projectPlugin.getVersion() != null ? projectPlugin.getVersion() : getCurrentMavenPluginVersion(groupId, artifactId);
                    }
                }

                if (currentVersion == null) {
                    // if still null skip version comparison
                    continue;
                }

                ComparableVersion currentVersionComparable = new ComparableVersion(currentVersion);

                if (latestVersionComparable.compareTo(currentVersionComparable) > 0) {
                    result.add(new DependencyUpdateResult(groupId, artifactId, currentVersion, latestVersion));
                }
            } catch (JSONException je) {
                // ignore
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

    private String getCurrentMavenPluginVersion(String groupId, String artifactId) {
        for (Plugin mavenPlugin : mavenPlugins) {
            if (mavenPlugin.getGroupId().equals(groupId) && mavenPlugin.getArtifactId().equals(artifactId)) {
                return mavenPlugin.getVersion();
            }
        }
        return null;
    }

}
