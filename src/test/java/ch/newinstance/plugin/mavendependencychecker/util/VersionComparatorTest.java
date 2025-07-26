package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.config.MavenDependencyCheckerSettings;
import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionComparatorTest {

    @Mock
    private MavenDependencyCheckerSettings settings;

    @InjectMocks
    private VersionComparator testee;

    // Dependencies //

    @Test
    void compareDependencyVersions_newVersionAvailable_shouldReturnResult() {
        when(settings.isMajorVersionChangeIgnored()).thenReturn(false);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertFalse(result.isEmpty());
        assertEquals("org.apache.commons", result.getFirst().getGroupId());
        assertEquals("commons-lang3", result.getFirst().getArtifactId());
        assertEquals("3.11.0", result.getFirst().getCurrentVersion());
        assertEquals("3.12.0", result.getFirst().getLatestVersion());
    }

    @Test
    void compareDependencyVersions_noVersionFound_shouldReturnEmptyResult() {
        testee = new VersionComparator(Collections.emptyList(), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_alreadyLatestVersionUsed_shouldReturnEmptyResult() {
        testee = new VersionComparator(Collections.emptyList(), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.12.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_wrongVersionUsed_shouldReturnEmptyResult() {
        when(settings.isMajorVersionChangeIgnored()).thenReturn(false);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.13.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_latestMavenVersionUsed_shouldReturnResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-lang3");
        dependency.setVersion("3.11.0");

        when(settings.isMajorVersionChangeIgnored()).thenReturn(false);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency(null)), List.of(dependency));
        assertFalse(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_noMavenVersionAvailable_shouldReturnEmptyResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-lang3");
        dependency.setVersion(null);

        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency(null)), List.of(dependency));
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_noMavenDependencyFound_shouldReturnEmptyResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-collections4");
        dependency.setVersion("4.4");

        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency(null)), List.of(dependency));
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_checkMinorAndPatchVersionsOnlyEnabled_shouldNotReturnResult() {
        when(settings.isMajorVersionChangeIgnored()).thenReturn(true);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v500()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_checkMinorAndPatchVersionsOnlyEnabledAndMinorVersionAvailable_shouldReturnResult() {
        when(settings.isMajorVersionChangeIgnored()).thenReturn(true);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120(), getResponse_apache_commons_lang3_v500()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertFalse(result.isEmpty());
        assertEquals("org.apache.commons", result.getFirst().getGroupId());
        assertEquals("commons-lang3", result.getFirst().getArtifactId());
        assertEquals("3.11.0", result.getFirst().getCurrentVersion());
        assertEquals("3.12.0", result.getFirst().getLatestVersion());
    }

    @Test
    void compareDependencyVersions_checkSnapshotVersion_shouldReturnResult() {
        when(settings.isPrereleaseVersionsExcluded()).thenReturn(true);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120_snapshot()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_checkDoubleDashVersion_shouldReturnResult() {
        when(settings.isPrereleaseVersionsExcluded()).thenReturn(true);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120_doubledash()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_checkTimestampVersion_shouldReturnResult() {
        when(settings.isPrereleaseVersionsExcluded()).thenReturn(true);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120_timestamp()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_checkLetterVersion_shouldReturnResult() {
        when(settings.isPrereleaseVersionsExcluded()).thenReturn(true);
        testee = new VersionComparator(List.of(getResponse_apache_commons_lang3_v3120_letter()), settings);
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    // Plugins //

    @Test
    void comparePluginVersions_newVersionAvailable_shouldReturnResult() {
        testee = new VersionComparator(List.of(getResponse_maven_compiler_plugin_v3110()), settings);
        List<DependencyUpdateResult> result = testee.comparePluginVersions(List.of(createMavenPlugin("3.10.0")), Collections.emptyList());
        assertFalse(result.isEmpty());
        assertEquals("org.apache.maven.plugins", result.getFirst().getGroupId());
        assertEquals("maven-compiler-plugin", result.getFirst().getArtifactId());
        assertEquals("3.10.0", result.getFirst().getCurrentVersion());
        assertEquals("3.11.0", result.getFirst().getLatestVersion());
    }

    @Test
    void comparePluginVersions_noVersionFound_shouldReturnEmptyResult() {
        testee = new VersionComparator(Collections.emptyList(), settings);
        List<DependencyUpdateResult> result = testee.comparePluginVersions(List.of(createMavenPlugin("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void comparePluginVersions_alreadyLatestVersionUsed_shouldReturnEmptyResult() {
        testee = new VersionComparator(Collections.emptyList(), settings);
        List<DependencyUpdateResult> result = testee.comparePluginVersions(List.of(createMavenPlugin("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void comparePluginVersions_latestMavenVersionUsed_shouldReturnResult() {
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-compiler-plugin");
        plugin.setVersion("3.10.0");

        testee = new VersionComparator(List.of(getResponse_maven_compiler_plugin_v3110()), settings);
        List<DependencyUpdateResult> result = testee.comparePluginVersions(List.of(createMavenPlugin(null)), List.of(plugin));
        assertFalse(result.isEmpty());
    }

    private String getResponse_apache_commons_lang3_v3120() {
        return createJson("org.apache.commons", "commons-lang3", "3.12.0");
    }

    private String getResponse_apache_commons_lang3_v3120_snapshot() {
        return createJson("org.apache.commons", "commons-lang3", "3.12.0-SNAPSHOT");
    }

    private String getResponse_apache_commons_lang3_v3120_timestamp() {
        return createJson("org.apache.commons", "commons-lang3", "3.12.0+20250719144700");
    }

    private String getResponse_apache_commons_lang3_v3120_letter() {
        return createJson("org.apache.commons", "commons-lang3", "3.12.0beta");
    }

    private String getResponse_apache_commons_lang3_v3120_doubledash() {
        return createJson("org.apache.commons", "commons-lang3", "3.12.0-beta-2");
    }

    private String getResponse_apache_commons_lang3_v500() {
        return createJson("org.apache.commons", "commons-lang3", "5.0.0");
    }

    private String getResponse_maven_compiler_plugin_v3110() {
        return createJson("org.apache.maven.plugins", "maven-compiler-plugin", "3.11.0");
    }

    private MavenArtifact createModuleDependency(String version) {
        return new MavenArtifact("org.apache.commons", "commons-lang3", version, null, null, null, null,
                false, null, null, null, false, false);
    }

    private String createJson(String group, String artifact, String version) {
        JSONObject dependency = new JSONObject();
        dependency.put("id", group + ":" + artifact);
        dependency.put("g", group);
        dependency.put("a", artifact);
        dependency.put("v", version);
        dependency.put("repositoryId", "central");
        dependency.put("p", "jar");

        JSONArray docsArray = new JSONArray();
        docsArray.put(dependency);

        JSONObject docs = new JSONObject();
        docs.put("docs", docsArray);

        JSONObject root = new JSONObject();
        root.put("response", docs);
        return root.toString();
    }

    private MavenPlugin createMavenPlugin(String version) {
        return new MavenPlugin("org.apache.maven.plugins", "maven-compiler-plugin", version,
                false, false, null, Collections.emptyList(), Collections.emptyList());
    }

}
