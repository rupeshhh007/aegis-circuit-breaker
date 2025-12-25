package com.example.aegis.service;


import com.example.aegis.aegis.client.AegisWebClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ExternalApiService {

    private final AegisWebClient client;

    public ExternalApiService(AegisWebClient client) {
        this.client = client;
    }

    public Mono<String> callExternal() {
        return client.get("https://httpstat.us/500");
    }
}

