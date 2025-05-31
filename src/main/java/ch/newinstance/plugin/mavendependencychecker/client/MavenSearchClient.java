package ch.newinstance.plugin.mavendependencychecker.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MavenSearchClient {

    private static final String MAVEN_BASE_SEARCH_URI = "https://search.maven.org/solrsearch/select?wt=json&q=";
    private static final String MAVEN_TIMESTAMP_SEARCH_URI = "https://search.maven.org/solrsearch/select?wt=json&core=gav&q=";

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .executor(executorService)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // list of artifacts that have to be searched by release date in order to get the latest (current) version
    private static final List<String> ARTIFACTS_BY_TIMESTAMP = List.of("commons-beanutils");

    public List<String> executeSearchQueries(List<String> searchQueries) {
        List<String> baseSearchQueries = new ArrayList<>();
        List<String> timestampSearchQueries = new ArrayList<>();
        for (String query : searchQueries) {
            boolean containsArtifact = ARTIFACTS_BY_TIMESTAMP.stream().anyMatch(query::contains);
            if (containsArtifact) {
                timestampSearchQueries.add(query);
            } else {
                baseSearchQueries.add(query);
            }
        }

        List<String> results = new ArrayList<>();
        results.addAll(executeBaseSearch(baseSearchQueries));
        results.addAll(executeTimestampSearch(timestampSearchQueries));
        return results;
    }

    private static List<String> executeBaseSearch(List<String> searchQueries) {
        List<CompletableFuture<String>> result = searchQueries.stream()
                .map(query -> httpClient.sendAsync(
                                HttpRequest.newBuilder(URI.create(MAVEN_BASE_SEARCH_URI + query)).GET().build(),
                                HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body))
                .toList();

        List<String> results = new ArrayList<>();
        for (CompletableFuture<String> future : result) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                // skip response
            }
        }
        return results;
    }

    private static List<String> executeTimestampSearch(List<String> searchQueries) {
        List<CompletableFuture<String>> result = searchQueries.stream()
                .map(query -> httpClient.sendAsync(
                                HttpRequest.newBuilder(URI.create(MAVEN_TIMESTAMP_SEARCH_URI + query)).GET().build(),
                                HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body))
                .toList();

        List<String> results = new ArrayList<>();
        for (CompletableFuture<String> future : result) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                // skip response
            }
        }
        return results;
    }

}
