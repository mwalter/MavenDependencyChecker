package ch.newinstance.plugin.mavendependencychecker.util;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyUpdateResult;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PomGeneratorTest {

    private final  PomGenerator pomGenerator = new PomGenerator();

    @Test
    void generatePom_emptyModel_shouldNotContainDependenciesSection() {
        String result = pomGenerator.generatePom(Collections.emptyList());

        assertFalse(result.contains("dependencies"));
    }

    @Test
    void generatePom_modelWithDependency_shouldContainDependenciesSection() {
        DependencyUpdateResult dependency = new DependencyUpdateResult("org.apache.commons", "commons-lang3", "3.8", "3.12.0", true);

        String result = pomGenerator.generatePom(List.of(dependency));

        assertTrue(result.contains("dependencies"));
        assertTrue(result.contains("commons-lang3"));
    }

    @Test
    void generatePom_modelWithPlugin_shouldContainPluginsSection() {
        DependencyUpdateResult plugin = new DependencyUpdateResult("org.springframework.boot", "spring-boot-maven-plugin", "3.3.1", "3.3.5", false);

        String result = pomGenerator.generatePom(List.of(plugin));

        assertTrue(result.contains("plugins"));
        assertTrue(result.contains("spring-boot-maven-plugin"));
    }

}