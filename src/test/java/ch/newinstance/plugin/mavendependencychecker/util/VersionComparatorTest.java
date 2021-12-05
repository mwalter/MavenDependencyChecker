package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionComparatorTest {

    private final VersionComparator testee = new VersionComparator(createModuleDependency());

    @Test
    public void compareVersions_validJson_shouldReturnResult() {
        List<DependencyUpdateResult> result = testee.compareVersions(List.of(getValidJson()));
        assertFalse(result.isEmpty());
        assertEquals("org.apache.commons", result.get(0).getGroupId());
        assertEquals("commons-lang3", result.get(0).getArtifactId());
        assertEquals("3.11.0", result.get(0).getCurrentVersion());
        assertEquals("3.12.0", result.get(0).getLatestVersion());
    }

    @Test
    public void compareVersions_emptyJson_shouldReturnEmptyResult() {
        List<DependencyUpdateResult> result = testee.compareVersions(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    private String getValidJson() {
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

    private Map<String, String> createModuleDependency() {
        Map<String, String> map = new HashMap<>();
        map.put("org.apache.commons:commons-lang3", "3.11.0");
        return map;
    }
}
