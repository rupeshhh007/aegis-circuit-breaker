package com.example.aegis.controller;



import com.example.aegis.service.ExternalApiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ExternalTestController {

    private final ExternalApiService service;

    public ExternalTestController(ExternalApiService service) {
        this.service = service;
    }

    @GetMapping("/external")
    public Mono<String> call() {
        return service.callExternal();
    }
}

