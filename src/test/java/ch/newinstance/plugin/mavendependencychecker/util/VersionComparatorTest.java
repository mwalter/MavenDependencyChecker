package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionComparatorTest {

    private VersionComparator testee;

    @Test
    void compareVersions_newVersionAvailable_shouldReturnResult() {
        testee = new VersionComparator(createModuleDependency("3.11.0"), Collections.emptyList());
        List<DependencyUpdateResult> result = testee.compareVersions(List.of(getResponse()));
        assertFalse(result.isEmpty());
        assertEquals("org.apache.commons", result.get(0).getGroupId());
        assertEquals("commons-lang3", result.get(0).getArtifactId());
        assertEquals("3.11.0", result.get(0).getCurrentVersion());
        assertEquals("3.12.0", result.get(0).getLatestVersion());
    }

    @Test
    void compareVersions_noVersionFound_shouldReturnEmptyResult() {
        testee = new VersionComparator(createModuleDependency("3.11.0"), Collections.emptyList());
        List<DependencyUpdateResult> result = testee.compareVersions(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareVersions_alreadyLatestVersionUsed_shouldReturnEmptyResult() {
        testee = new VersionComparator(createModuleDependency("3.12.0"), Collections.emptyList());
        List<DependencyUpdateResult> result = testee.compareVersions(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void compareVersions_wrongVersionUsed_shouldReturnEmptyResult() {
        testee = new VersionComparator(createModuleDependency("3.13.0"), Collections.emptyList());
        List<DependencyUpdateResult> result = testee.compareVersions(List.of(getResponse()));
        assertTrue(result.isEmpty());
    }

    @Test
    void compareVersions_latestMavenVersionUsed_shouldReturnResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-lang3");
        dependency.setVersion("3.11.0");

        testee = new VersionComparator(createModuleDependency(null), List.of(dependency));
        List<DependencyUpdateResult> result = testee.compareVersions(List.of(getResponse()));
        assertFalse(result.isEmpty());
    }

    @Test
    void compareVersions_noMavenVersionAvailable_shouldReturnEmptyResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-lang3");
        dependency.setVersion(null);

        testee = new VersionComparator(createModuleDependency(null), List.of(dependency));
        List<DependencyUpdateResult> result = testee.compareVersions(List.of(getResponse()));
        assertTrue(result.isEmpty());
    }

    @Test
    void compareVersions_noMavenDependencyFound_shouldReturnEmptyResult() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-collections4");
        dependency.setVersion("4.4");

        testee = new VersionComparator(createModuleDependency(null), List.of(dependency));
        List<DependencyUpdateResult> result = testee.compareVersions(List.of(getResponse()));
        assertTrue(result.isEmpty());
    }

    private String getResponse() {
        return "{" +
                "\"response\": {" +
                "\"docs\": [" +
                "{" +
                "\"id\": \"org.apache.commons:commons-lang3\"," +
                "\"g\": \"org.apache.commons\"," +
                "\"a\": \"commons-lang3\"," +
                "\"latestVersion\": \"3.12.0\"," +
                "\"repositoryId\": \"central\"," +
                "\"p\": \"jar\"" +
                "}" +
                "]" +
                "}" +
                "}";
    }

    private Map<String, String> createModuleDependency(String version) {
        Map<String, String> map = new HashMap<>();
        map.put("org.apache.commons:commons-lang3", version);
        return map;
    }
}
