package com.example.aegis.config;

import com.example.aegis.aegis.client.AegisWebClient;
import com.example.aegis.aegis.engine.AegisEngine;
import com.example.aegis.aegis.policy.CircuitBreakerPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class AegisConfig {

    @Bean
    public AegisEngine aegisEngine() {
        return new AegisEngine(
                new CircuitBreakerPolicy(
                        5,
                        10,
                        Duration.ofSeconds(10)
                )
        );
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public AegisWebClient aegisWebClient(
            WebClient webClient,
            AegisEngine aegisEngine) {

        return new AegisWebClient(webClient, aegisEngine);
    }
}