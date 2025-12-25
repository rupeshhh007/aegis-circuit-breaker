package com.example.aegis.aegis.policy;
import java.time.Duration;
public class CircuitBreakerPolicy {

    private final int failureThreshold;
    private final int minimumRequests;
    private final Duration openCooldown;

    public CircuitBreakerPolicy(
            int failureThreshold,
            int minimumRequests,
            Duration openCooldown) {

        this.failureThreshold = failureThreshold;
        this.minimumRequests = minimumRequests;
        this.openCooldown = openCooldown;
    }

    public boolean shouldOpen(int failures, int totalRequests) {
        if (totalRequests < minimumRequests) {
            return false;
        }
        return failures >= failureThreshold;
    }

    public Duration cooldown() {
        return openCooldown;
    }
}