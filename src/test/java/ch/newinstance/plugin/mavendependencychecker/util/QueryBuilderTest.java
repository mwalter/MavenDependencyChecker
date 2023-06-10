package ch.newinstance.plugin.mavendependencychecker.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.junit.jupiter.api.Test;

class QueryBuilderTest {

    private final QueryBuilder testee = new QueryBuilder();
    
    @Test
    void buildQueries_emptyDependencyList_shouldReturnEmptyList() {
        List<String> result = testee.buildDependencyQueries(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void buildQueries_oneDependency_shouldReturnOneQuery() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("org.apache.commons");
        dependency.setArtifactId("commons-lang3");
        List<String> result = testee.buildDependencyQueries(List.of(dependency));
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

        List<String> result = testee.buildDependencyQueries(List.of(dependency1, dependency2));
        assertEquals(2, result.size());
    }

    @Test
    void buildQueries_emptyPluginList_shouldReturnEmptyList() {
        List<String> result = testee.buildPluginQueries(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void buildQueries_onePlugin_shouldReturnOneQuery() {
        Plugin plugin = new Plugin();
        plugin.setGroupId("org.apache.maven.plugins");
        plugin.setArtifactId("maven-compiler-plugin");
        List<String> result = testee.buildPluginQueries(List.of(plugin));
        assertEquals(1, result.size());
        assertEquals("g:%20org.apache.maven.plugins%20+AND+a:%20maven-compiler-plugin%20", result.get(0));
    }

    @Test
    void buildQueries_twoPlugins_shouldReturnTwoQueries() {
        Plugin plugin1 = new Plugin();
        plugin1.setGroupId("org.apache.maven.plugins");
        plugin1.setArtifactId("maven-compiler-plugin");
        Plugin plugin2 = new Plugin();
        plugin2.setGroupId("org.springframework.boot");
        plugin2.setArtifactId("spring-boot-maven-plugin");

        List<String> result = testee.buildPluginQueries(List.of(plugin1, plugin2));
        assertEquals(2, result.size());
    }

}
