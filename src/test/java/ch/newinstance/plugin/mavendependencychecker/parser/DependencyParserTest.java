package ch.newinstance.plugin.mavendependencychecker.parser;

import com.intellij.psi.PsiFile;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @InjectMocks
    private DependencyParser parser;

    @Test
    void parseMavenDependencies_emptyPom_shouldReturnEmptyCollection() {
        when(pomFile.getText()).thenReturn("");

        List<Dependency> result = parser.parseMavenDependencies();
        assertTrue(result.isEmpty());
    }

    @Test
    void parseMavenDependencies_someDependencies_shouldReturnDependencies() {
        when(pomFile.getText()).thenReturn(readPomFile());

        List<Dependency> result = parser.parseMavenDependencies();
        assertFalse(result.isEmpty());
        assertEquals(8, result.size());
    }

    @Test
    void parseMavenDependencies_dependencyVersionWithPlaceholder_shouldReturnVersionFromPropertiesSection() {
        when(pomFile.getText()).thenReturn(readPomFile());

        List<Dependency> result = parser.parseMavenDependencies();
        Optional<Dependency> dependencyWithReplacedVersion = result.stream()
                .filter(dependency -> dependency.getArtifactId().equals("spring-cloud-dependencies"))
                .findFirst();
        assertTrue(dependencyWithReplacedVersion.isPresent());
        assertEquals("2021.0.1", dependencyWithReplacedVersion.get().getVersion());
    }

    @Test
    void parseMavenDependencies_dependencyGropudIdWithPlaceholder_shouldReturnGroupNameFromPropertiesSection() {
        when(pomFile.getText()).thenReturn(readPomFile());

        List<Dependency> result = parser.parseMavenDependencies();
        Optional<Dependency> dependencyWithReplacedGroupId = result.stream()
                .filter(dependency -> dependency.getArtifactId().equals("lombok"))
                .findFirst();
        assertTrue(dependencyWithReplacedGroupId.isPresent());
        assertEquals("org.projectlombok", dependencyWithReplacedGroupId.get().getGroupId());
    }

    @Test
    void parseMavenDependencies_dependencyArtifactIdWithPlaceholder_shouldReturnArtifactNameFromPropertiesSection() {
        when(pomFile.getText()).thenReturn(readPomFile());

        List<Dependency> result = parser.parseMavenDependencies();
        Optional<Dependency> dependencyWithReplacedArtifactId = result.stream()
                .filter(dependency -> dependency.getGroupId().equals("org.apache.commons"))
                .findFirst();
        assertTrue(dependencyWithReplacedArtifactId.isPresent());
        assertEquals("commons-lang3", dependencyWithReplacedArtifactId.get().getArtifactId());
    }

    @Test
    void parseMavenPlugins_emptyPom_shouldReturnEmptyCollection() {
        when(pomFile.getText()).thenReturn("");

        List<Plugin> result = parser.parseMavenPlugins();
        assertTrue(result.isEmpty());
    }

    @Test
    void parseMavenPlugins_somePlugins_shouldReturnPlugins() {
        when(pomFile.getText()).thenReturn(readPomFile());

        List<Plugin> result = parser.parseMavenPlugins();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
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