package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
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
    private final boolean isCheckMinorAndPatchVersionsOnly;

    public VersionComparator(List<String> queryResults, boolean isCheckMinorAndPatchVersionsOnly) {
        this.queryResults = queryResults;
        this.isCheckMinorAndPatchVersionsOnly = isCheckMinorAndPatchVersionsOnly;
    }

    public List<DependencyUpdateResult> compareDependencyVersions(List<MavenArtifact> moduleDependencies, List<Dependency> mavenDependencies) {
        List<DependencyUpdateResult> result = new ArrayList<>();
        for (String json : queryResults) {
            try {
                DependencyInfo dependencyInfo = extractVersionInfoFromJson(json);
                ComparableVersion latestVersionComparable = new ComparableVersion(dependencyInfo.latestVersion());

                // if not managed by IntelliJ use current POM version instead
                String currentVersion = null;
                for (MavenArtifact mavenArtifact : moduleDependencies) {
                    if (mavenArtifact.getGroupId().equals(dependencyInfo.groupId()) && mavenArtifact.getArtifactId().equals(dependencyInfo.artifactId())) {
                        currentVersion = mavenArtifact.getVersion() != null ? mavenArtifact.getVersion() :
                                getCurrentMavenDependencyVersion(mavenDependencies, dependencyInfo.groupId(), dependencyInfo.artifactId());
                    }
                }

                if (currentVersion == null) {
                    // if still null skip version comparison
                    continue;
                }

                if (isCheckMinorAndPatchVersionsOnly) {
                    DefaultArtifactVersion currentDefaultVersion = new DefaultArtifactVersion(currentVersion);
                    DefaultArtifactVersion latestDefaultVersion = new DefaultArtifactVersion(dependencyInfo.latestVersion());
                    if (latestDefaultVersion.getMajorVersion() > currentDefaultVersion.getMajorVersion()) {
                        // here we do not want to add major updates to the list of results
                        continue;
                    }
                }

                ComparableVersion currentVersionComparable = new ComparableVersion(currentVersion);
                if (latestVersionComparable.compareTo(currentVersionComparable) > 0) {
                    result.add(new DependencyUpdateResult(dependencyInfo.groupId(), dependencyInfo.artifactId(), currentVersion, dependencyInfo.latestVersion(), true));
                }
            } catch (JSONException je) {
                // ignore
            }
        }
        return result;
    }

    public List<DependencyUpdateResult> comparePluginVersions(List<MavenPlugin> projectPlugins, List<Plugin> mavenPlugins) {
        List<DependencyUpdateResult> result = new ArrayList<>();
        for (String json : queryResults) {
            try {
                DependencyInfo dependencyInfo = extractVersionInfoFromJson(json);
                ComparableVersion latestVersionComparable = new ComparableVersion(dependencyInfo.latestVersion());

                // if not managed by IntelliJ use current POM version instead
                String currentVersion = null;
                for (MavenPlugin projectPlugin : projectPlugins) {
                    if (projectPlugin.getGroupId().equals(dependencyInfo.groupId()) && projectPlugin.getArtifactId().equals(dependencyInfo.artifactId())) {
                        currentVersion = projectPlugin.getVersion() != null ? projectPlugin.getVersion() :
                                getCurrentMavenPluginVersion(mavenPlugins, dependencyInfo.groupId(), dependencyInfo.artifactId());
                    }
                }

                if (currentVersion == null) {
                    // if still null skip version comparison
                    continue;
                }

                ComparableVersion currentVersionComparable = new ComparableVersion(currentVersion);
                if (latestVersionComparable.compareTo(currentVersionComparable) > 0) {
                    result.add(new DependencyUpdateResult(dependencyInfo.groupId(), dependencyInfo.artifactId(), currentVersion, dependencyInfo.latestVersion(), false));
                }
            } catch (JSONException je) {
                // ignore
            }
        }

        return result;
    }

    private String getCurrentMavenDependencyVersion(List<Dependency> mavenDependencies, String groupId, String artifactId) {
        for (Dependency mavenDependency : mavenDependencies) {
            if (mavenDependency.getGroupId().equals(groupId) && mavenDependency.getArtifactId().equals(artifactId)) {
                return mavenDependency.getVersion();
            }
        }
        return null;
    }

    private String getCurrentMavenPluginVersion(List<Plugin> mavenPlugins, String groupId, String artifactId) {
        for (Plugin mavenPlugin : mavenPlugins) {
            if (mavenPlugin.getGroupId().equals(groupId) && mavenPlugin.getArtifactId().equals(artifactId)) {
                return mavenPlugin.getVersion();
            }
        }
        return null;
    }

    private DependencyInfo extractVersionInfoFromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject response = jsonObject.getJSONObject(RESPONSE);
        JSONObject docsObject = response.getJSONArray(DOCS_ARRAY).getJSONObject(0);
        return new DependencyInfo(docsObject.getString(GROUP), docsObject.getString(ARTIFACT), docsObject.getString(VERSION));
    }

    private record DependencyInfo(String groupId, String artifactId, String latestVersion) {
    }
}
