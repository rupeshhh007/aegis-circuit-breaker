package com.example.aegis.aegis.client;



import com.example.aegis.aegis.engine.AegisEngine;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class AegisWebClient {

    private final WebClient webClient;
    private final AegisEngine aegis;

    public AegisWebClient(WebClient webClient, AegisEngine aegis) {
        this.webClient = webClient;
        this.aegis = aegis;
    }

    public Mono<String> get(String url) {

        if (!aegis.allowRequest()) {
            return Mono.just("Circuit OPEN – request blocked");
        }

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(res -> aegis.recordSuccess())
                .doOnError(err -> aegis.recordFailure())
                .onErrorResume(err ->
                        Mono.just("External service failed")
                );
    }

}

