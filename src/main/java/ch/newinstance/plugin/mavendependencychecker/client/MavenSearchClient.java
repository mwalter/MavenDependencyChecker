package ch.newinstance.plugin.mavendependencychecker.client;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class MavenSearchClient {

    private static final String MAVEN_BASE_SEARCH_URI = "https://search.maven.org/solrsearch/select?wt=json&q=";

    private final HttpClient searchClient = HttpClient.create();

    public List<String> executeSearchQueries(List<String> searchQueries) {
        return Flux.fromIterable(searchQueries)
                .flatMap(this::executeSearchQuery)
                .collectList()
                .block();
    }

    private Mono<String> executeSearchQuery(String searchQuery) {
        return searchClient.get()
                .uri(MAVEN_BASE_SEARCH_URI + searchQuery)
                .responseContent()
                .aggregate()
                .asString();
    }

}
