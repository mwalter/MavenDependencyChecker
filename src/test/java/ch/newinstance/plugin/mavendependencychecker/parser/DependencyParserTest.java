package ch.newinstance.plugin.mavendependencychecker.parser;

import ch.newinstance.plugin.mavendependencychecker.model.DependencyParseResult;
import com.intellij.psi.PsiFile;
import org.apache.maven.model.Dependency;
import org.jetbrains.idea.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DependencyParserTest {

    @Mock
    private PsiFile pomFile;

    @Mock
    private List<MavenProject> mavenProjects;

    @Mock
    private MavenProject mavenProject;

    @BeforeEach
    void setup() {
        when(mavenProjects.size()).thenReturn(1);
        when(mavenProjects.getFirst()).thenReturn(mavenProject);
    }

    @Test
    void parseMavenDependencies_emptyPom_shouldReturnEmptyCollection() {
        when(pomFile.getText()).thenReturn("");

        DependencyParseResult result = DependencyParser.parseDependencies(pomFile, mavenProjects);
        assertTrue(result.dependencies().isEmpty());
    }

    @Test
    void parseMavenDependencies_someDependencies_shouldReturnDependencies() {
        when(pomFile.getText()).thenReturn(readPomFile());

        DependencyParseResult result = DependencyParser.parseDependencies(pomFile, mavenProjects);
        assertFalse(result.dependencies().isEmpty());
        assertEquals(8, result.dependencies().size());
    }

    @Test
    void parseMavenDependencies_dependencyVersionWithPlaceholder_shouldReturnVersionFromPropertiesSection() {
        when(pomFile.getText()).thenReturn(readPomFile());

        DependencyParseResult result = DependencyParser.parseDependencies(pomFile, mavenProjects);
        Optional<Dependency> dependencyWithReplacedVersion = result.dependencies().stream()
                .filter(dependency -> dependency.getArtifactId().equals("spring-cloud-dependencies"))
                .findFirst();
        assertTrue(dependencyWithReplacedVersion.isPresent());
        assertEquals("2021.0.1", dependencyWithReplacedVersion.get().getVersion());
    }

    @Test
    void parseMavenDependencies_dependencyGropudIdWithPlaceholder_shouldReturnGroupNameFromPropertiesSection() {
        when(pomFile.getText()).thenReturn(readPomFile());

        DependencyParseResult result = DependencyParser.parseDependencies(pomFile, mavenProjects);
        Optional<Dependency> dependencyWithReplacedGroupId = result.dependencies().stream()
                .filter(dependency -> dependency.getArtifactId().equals("lombok"))
                .findFirst();
        assertTrue(dependencyWithReplacedGroupId.isPresent());
        assertEquals("org.projectlombok", dependencyWithReplacedGroupId.get().getGroupId());
    }

    @Test
    void parseMavenDependencies_dependencyArtifactIdWithPlaceholder_shouldReturnArtifactNameFromPropertiesSection() {
        when(pomFile.getText()).thenReturn(readPomFile());

        DependencyParseResult result = DependencyParser.parseDependencies(pomFile, mavenProjects);
        Optional<Dependency> dependencyWithReplacedArtifactId = result.dependencies().stream()
                .filter(dependency -> dependency.getGroupId().equals("org.apache.commons"))
                .findFirst();
        assertTrue(dependencyWithReplacedArtifactId.isPresent());
        assertEquals("commons-lang3", dependencyWithReplacedArtifactId.get().getArtifactId());
    }

    @Test
    void parseMavenPlugins_emptyPom_shouldReturnEmptyCollection() {
        when(pomFile.getText()).thenReturn("");

        DependencyParseResult result = DependencyParser.parseDependencies(pomFile, mavenProjects);
        assertTrue(result.plugins().isEmpty());
    }

    @Test
    void parseMavenPlugins_somePlugins_shouldReturnPlugins() {
        when(pomFile.getText()).thenReturn(readPomFile());

        DependencyParseResult result = DependencyParser.parseDependencies(pomFile, mavenProjects);
        assertFalse(result.plugins().isEmpty());
        assertEquals(1, result.plugins().size());
    }


    private String readPomFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("pom.xml");
        if (resource == null) {
            throw new IllegalArgumentException("File not found.");
        }

        File file;
        try {
            file = new File(resource.toURI());
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (URISyntaxException | IOException e) {
            return "";
        }
    }

}