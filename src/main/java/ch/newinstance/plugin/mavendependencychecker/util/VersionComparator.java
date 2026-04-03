package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.config.MavenDependencyCheckerSettings;
import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Optional;

public class VersionComparator {

    private static final String ARTIFACT = "a";
    private static final String DOCS_ARRAY = "docs";
    private static final String GROUP = "g";
    private static final String LATEST_VERSION = "latestVersion";
    private static final String RESPONSE = "response";
    private static final String VERSION = "v";

    private final List<String> queryResults;
    private final MavenDependencyCheckerSettings settings;

    public VersionComparator(List<String> queryResults, MavenDependencyCheckerSettings settings) {
        this.queryResults = queryResults;
        this.settings = settings;
    }

    public List<DependencyUpdateResult> compareDependencyVersions(List<MavenArtifact> moduleDependencies, List<Dependency> mavenDependencies) {
        List<DependencyUpdateResult> result = new ArrayList<>();
        for (String json : queryResults) {
            try {
                DependencyInfo dependencyInfo = extractVersionInfoFromJson(json);
                ComparableVersion latestVersionComparable = new ComparableVersion(dependencyInfo.latestVersion());

                // if not managed by IntelliJ use current POM version instead
                Optional<String> currentVersion = extractCurrentDependencyVersionFromPom(moduleDependencies, mavenDependencies, dependencyInfo);
                if (currentVersion.isEmpty()) {
                    // if not available skip version comparison
                    continue;
                }

                if (settings.isMajorVersionChangeIgnored()) {
                    DefaultArtifactVersion currentDefaultVersion = new DefaultArtifactVersion(currentVersion.get());
                    DefaultArtifactVersion latestDefaultVersion = new DefaultArtifactVersion(dependencyInfo.latestVersion());
                    if (latestDefaultVersion.getMajorVersion() > currentDefaultVersion.getMajorVersion()) {
                        // here we do not want to add major updates to the list of results
                        continue;
                    }
                }

                if (isPrereleaseVersionExcluded(settings.isPrereleaseVersionsExcluded(), dependencyInfo.latestVersion())) {
                    continue;
                }

                if (isNewerVersionAvailable(latestVersionComparable, currentVersion.get())) {
                    result.add(new DependencyUpdateResult(dependencyInfo.groupId(), dependencyInfo.artifactId(), currentVersion.get(), dependencyInfo.latestVersion(), true));
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
                Optional<String> currentVersion = extractCurrentPluginVersionFromPom(projectPlugins, mavenPlugins, dependencyInfo);
                if (currentVersion.isEmpty()) {
                    // if not available skip version comparison
                    continue;
                }

                if (isPrereleaseVersionExcluded(settings.isPrereleaseVersionsExcluded(), dependencyInfo.latestVersion())) {
                    continue;
                }

                if (isNewerVersionAvailable(latestVersionComparable, currentVersion.get())) {
                    result.add(new DependencyUpdateResult(dependencyInfo.groupId(), dependencyInfo.artifactId(), currentVersion.get(), dependencyInfo.latestVersion(), true));
                }
            } catch (JSONException je) {
                // ignore
            }
        }

        return result;
    }

    private Optional<String> extractCurrentDependencyVersionFromPom(List<MavenArtifact> moduleDependencies, List<Dependency> mavenDependencies, DependencyInfo dependencyInfo) {
        for (MavenArtifact mavenArtifact : moduleDependencies) {
            if (mavenArtifact.getGroupId().equals(dependencyInfo.groupId()) && mavenArtifact.getArtifactId().equals(dependencyInfo.artifactId())) {
                return mavenArtifact.getVersion() != null ? Optional.of(mavenArtifact.getVersion()) :
                        getCurrentMavenDependencyVersion(mavenDependencies, dependencyInfo.groupId(), dependencyInfo.artifactId());
            }
        }
        return Optional.empty();
    }

    private Optional<String> getCurrentMavenDependencyVersion(List<Dependency> mavenDependencies, String groupId, String artifactId) {
        for (Dependency mavenDependency : mavenDependencies) {
            if (mavenDependency.getGroupId().equals(groupId) && mavenDependency.getArtifactId().equals(artifactId) && StringUtils.isNotBlank(mavenDependency.getVersion())) {
                return Optional.of(mavenDependency.getVersion());
            }
        }
        return Optional.empty();
    }

    private Optional<String> extractCurrentPluginVersionFromPom(List<MavenPlugin> projectPlugins, List<Plugin> mavenPlugins, DependencyInfo dependencyInfo) {
        for (MavenPlugin projectPlugin : projectPlugins) {
            if (projectPlugin.getGroupId().equals(dependencyInfo.groupId()) && projectPlugin.getArtifactId().equals(dependencyInfo.artifactId())) {
                return projectPlugin.getVersion() != null ? Optional.of(projectPlugin.getVersion()) :
                        getCurrentMavenPluginVersion(mavenPlugins, dependencyInfo.groupId(), dependencyInfo.artifactId());
            }
        }
        return Optional.empty();
    }

    private Optional<String> getCurrentMavenPluginVersion(List<Plugin> mavenPlugins, String groupId, String artifactId) {
        for (Plugin mavenPlugin : mavenPlugins) {
            if (mavenPlugin.getGroupId().equals(groupId) && mavenPlugin.getArtifactId().equals(artifactId)) {
                return Optional.of(mavenPlugin.getVersion());
            }
        }
        return Optional.empty();
    }

    private DependencyInfo extractVersionInfoFromJson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject response = jsonObject.getJSONObject(RESPONSE);
        JSONObject docsObject = response.getJSONArray(DOCS_ARRAY).getJSONObject(0);
        if (docsObject.has(LATEST_VERSION)) {
            return new DependencyInfo(docsObject.getString(GROUP), docsObject.getString(ARTIFACT), docsObject.getString(LATEST_VERSION));
        } else {
            return new DependencyInfo(docsObject.getString(GROUP), docsObject.getString(ARTIFACT), docsObject.getString(VERSION));
        }
    }

    private boolean isNewerVersionAvailable(ComparableVersion latestVersionComparable, String currentVersion) {
        ComparableVersion currentVersionComparable = new ComparableVersion(currentVersion);
        return latestVersionComparable.compareTo(currentVersionComparable) > 0;
    }

    private boolean isPrereleaseVersionExcluded(boolean isExcluded, String latestVersion) {
        return isExcluded && containsPrereleaseOrBuildVersion(latestVersion);
    }

    private boolean containsPrereleaseOrBuildVersion(String latestVersion) {
        return latestVersion.contains("+") || latestVersion.contains("-") || latestVersion.matches(".*[a-zA-Z]+.*");
    }

    private record DependencyInfo(String groupId, String artifactId, String latestVersion) {
    }
}
