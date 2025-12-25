package com.example.aegis.aegis.metrics;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeBucket {

    private final Instant timestamp;
    private final AtomicInteger success = new AtomicInteger(0);
    private final AtomicInteger failure = new AtomicInteger(0);

    public TimeBucket(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void recordSuccess() {
        success.incrementAndGet();
    }

    public void recordFailure() {
        failure.incrementAndGet();
    }

    public int getSuccess() {
        return success.get();
    }

    public int getFailure() {
        return failure.get();
    }
}
