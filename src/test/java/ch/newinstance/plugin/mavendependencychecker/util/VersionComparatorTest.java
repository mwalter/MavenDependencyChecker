package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenPlugin;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionComparatorTest {

    private VersionComparator testee;

    @Test
    void compareDependencyVersions_newVersionAvailable_shouldReturnResult() {
        testee = new VersionComparator(List.of(getDependencyResponse()));
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertFalse(result.isEmpty());
        assertEquals("org.apache.commons", result.get(0).getGroupId());
        assertEquals("commons-lang3", result.get(0).getArtifactId());
        assertEquals("3.11.0", result.get(0).getCurrentVersion());
        assertEquals("3.12.0", result.get(0).getLatestVersion());
    }

    @Test
    void compareDependencyVersions_noVersionFound_shouldReturnEmptyResult() {
        testee = new VersionComparator(Collections.emptyList());
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_alreadyLatestVersionUsed_shouldReturnEmptyResult() {
        testee = new VersionComparator(Collections.emptyList());
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.12.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_wrongVersionUsed_shouldReturnEmptyResult() {
        testee = new VersionComparator(List.of(getDependencyResponse()));
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency("3.13.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_latestMavenVersionUsed_shouldReturnResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-lang3");
        dependency.setVersion("3.11.0");

        testee = new VersionComparator(List.of(getDependencyResponse()));
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency(null)), List.of(dependency));
        assertFalse(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_noMavenVersionAvailable_shouldReturnEmptyResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-lang3");
        dependency.setVersion(null);

        testee = new VersionComparator(List.of(getDependencyResponse()));
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency(null)), List.of(dependency));
        assertTrue(result.isEmpty());
    }

    @Test
    void compareDependencyVersions_noMavenDependencyFound_shouldReturnEmptyResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-collections4");
        dependency.setVersion("4.4");

        testee = new VersionComparator(List.of(getDependencyResponse()));
        List<DependencyUpdateResult> result = testee.compareDependencyVersions(List.of(createModuleDependency(null)), List.of(dependency));
        assertTrue(result.isEmpty());
    }

    @Test
    void comparePluginVersions_newVersionAvailable_shouldReturnResult() {
        testee = new VersionComparator(List.of(getPluginResponse()));
        List<DependencyUpdateResult> result = testee.comparePluginVersions(List.of(createMavenPlugin("3.10.0")), Collections.emptyList());
        assertFalse(result.isEmpty());
        assertEquals("org.apache.maven.plugins", result.get(0).getGroupId());
        assertEquals("maven-compiler-plugin", result.get(0).getArtifactId());
        assertEquals("3.10.0", result.get(0).getCurrentVersion());
        assertEquals("3.11.0", result.get(0).getLatestVersion());
    }

    @Test
    void comparePluginVersions_noVersionFound_shouldReturnEmptyResult() {
        testee = new VersionComparator(Collections.emptyList());
        List<DependencyUpdateResult> result = testee.comparePluginVersions(List.of(createMavenPlugin("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void comparePluginVersions_alreadyLatestVersionUsed_shouldReturnEmptyResult() {
        testee = new VersionComparator(Collections.emptyList());
        List<DependencyUpdateResult> result = testee.comparePluginVersions(List.of(createMavenPlugin("3.11.0")), Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void comparePluginVersions_latestMavenVersionUsed_shouldReturnResult() {
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-compiler-plugin");
        plugin.setVersion("3.10.0");

        testee = new VersionComparator(List.of(getPluginResponse()));
        List<DependencyUpdateResult> result = testee.comparePluginVersions(List.of(createMavenPlugin(null)), List.of(plugin));
        assertFalse(result.isEmpty());
    }

    private String getDependencyResponse() {
        return "{" +
                "\"response\": {" +
                "\"docs\": [" +
                "{" +
                "\"id\": \"org.apache.commons:commons-lang3\"," +
                "\"g\": \"org.apache.commons\"," +
                "\"a\": \"commons-lang3\"," +
                "\"v\": \"3.12.0\"," +
                "\"repositoryId\": \"central\"," +
                "\"p\": \"jar\"" +
                "}" +
                "]" +
                "}" +
                "}";
    }

    private MavenArtifact createModuleDependency(String version) {
        return new MavenArtifact("org.apache.commons", "commons-lang3", version, null, null, null, null,
                false, null, null, null, false, false);
    }

    private String getPluginResponse() {
        return "{" +
                "\"response\": {" +
                "\"docs\": [" +
                "{" +
                "\"id\": \"org.apache.maven.plugins:maven-compiler-plugin\"," +
                "\"g\": \"org.apache.maven.plugins\"," +
                "\"a\": \"maven-compiler-plugin\"," +
                "\"v\": \"3.11.0\"," +
                "\"repositoryId\": \"central\"," +
                "\"p\": \"jar\"" +
                "}" +
                "]" +
                "}" +
                "}";
    }

    private MavenPlugin createMavenPlugin(String version) {
        return new MavenPlugin("org.apache.maven.plugins", "maven-compiler-plugin", version,
                false, false, null, Collections.emptyList(), Collections.emptyList());
    }
}
