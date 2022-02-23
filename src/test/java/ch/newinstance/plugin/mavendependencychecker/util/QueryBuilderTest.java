package ch.newinstance.plugin.mavendependencychecker.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.junit.jupiter.api.Test;

class QueryBuilderTest {

    private final QueryBuilder testee = new QueryBuilder();
    
    @Test
    void buildQueries_emptyList_shouldReturnEmptyList() {
        List<String> result = testee.buildQueries(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void buildQueries_oneDependency_shouldReturnOneQuery() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-lang3");
        List<String> result = testee.buildQueries(List.of(dependency));
        assertEquals(1, result.size());
        assertEquals("g:%20org.apache.commons%20+AND+a:%20commons-lang3%20", result.get(0));
    }

    @Test
    void buildQueries_twoDependencies_shouldReturnTwoQueries() {
        Dependency dependency1 = new Dependency();
        dependency1.setGroupId("org.apache.commons");
        dependency1.setArtifactId("commons-lang3");
        Dependency dependency2 = new Dependency();
        dependency2.setGroupId("org.apache.commons");
        dependency2.setArtifactId("commons-collections4");

        List<String> result = testee.buildQueries(List.of(dependency1, dependency2));
        assertEquals(2, result.size());
    }
    
}
