package ch.newinstance.plugin.mavendependencychecker.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenSearchClientTest {

    private final MockWebServer mockWebServer = new MockWebServer();

    private final MavenSearchClient testee = new MavenSearchClient();

    @Test
    void executeSearchQueries_emptyList_shouldReturnNoResult() {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("")
                .setResponseCode(200));

        List<String> result = testee.executeSearchQueries(Collections.emptyList());

        assertTrue(result.isEmpty());
    }

    @Test
    void executeSearchQueries_withQuery_shouldReturnResult() {
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(readPResultFile())
                .setResponseCode(200));
        String query = "g:%20org.apache.commons%20+AND+a:%20commons-lang3%20";

        List<String> result = testee.executeSearchQueries(List.of(query));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    private String readPResultFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("commons-lang-result.json");
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