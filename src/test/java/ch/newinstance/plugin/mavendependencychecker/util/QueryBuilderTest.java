package ch.newinstance.plugin.mavendependencychecker.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdate;
import org.junit.jupiter.api.Test;

public class QueryBuilderTest {

    private final QueryBuilder testee = new QueryBuilder();
    
    @Test
    public void buildQueries_emptyList_shouldReturnEmptyList() {
        List<String> result = testee.buildQueries(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void buildQueries_oneDependency_shouldReturnOneQuery() {
        DependencyUpdate dependencyUpdate = new DependencyUpdate("org.apache.commons", "commons-lang3");
        List<String> result = testee.buildQueries(List.of(dependencyUpdate));
        assertEquals(1, result.size());
        assertEquals("g:%20org.apache.commons%20+AND+a:%20commons-lang3%20", result.get(0));
    }

    @Test
    public void buildQueries_twoDependencies_shouldReturnTwoQueries() {
        DependencyUpdate dependencyUpdate1 = new DependencyUpdate("org.apache.commons", "commons-lang3");
        DependencyUpdate dependencyUpdate2 = new DependencyUpdate("org.apache.commons", "commons-collections4");
        List<String> result = testee.buildQueries(List.of(dependencyUpdate1, dependencyUpdate2));
        assertEquals(2, result.size());
    }
    
}
