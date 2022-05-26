package ch.newinstance.plugin.mavendependencychecker.parser;

import com.intellij.psi.PsiFile;
import org.apache.maven.model.Dependency;
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
        assertEquals(6, result.size());
    }

    @Test
    void parseMavenDependencies_oneDependencyWithPlaceholder_shouldReturnVersionFromPropertiesSection() {
        when(pomFile.getText()).thenReturn(readPomFile());

        List<Dependency> result = parser.parseMavenDependencies();
        Optional<Dependency> dependencyWithReplacedVersion = result.stream()
                .filter(dependency -> dependency.getArtifactId().equals("spring-cloud-dependencies"))
                .findFirst();
        assertTrue(dependencyWithReplacedVersion.isPresent());
        assertEquals("2021.0.1", dependencyWithReplacedVersion.get().getVersion());
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