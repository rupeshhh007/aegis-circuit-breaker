package com.example.aegis.aegis.state;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class CircuitStateHolder {

    private final AtomicReference<CircuitState> state =
            new AtomicReference<>(CircuitState.CLOSED);

    private volatile Instant lastStateChange = Instant.now();

    public CircuitState getState() {
        return state.get();
    }

    public void transitionTo(CircuitState newState) {
        state.set(newState);
        lastStateChange = Instant.now();
    }

    public boolean canAttemptReset(Duration cooldown) {
        return state.get() == CircuitState.OPEN &&
                Duration.between(lastStateChange, Instant.now())
                        .compareTo(cooldown) >= 0;
    }

    public Instant getLastStateChange() {
        return lastStateChange;
    }
}