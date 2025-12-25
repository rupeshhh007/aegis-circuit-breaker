package com.example.aegis.aegis.metrics;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

public class SlidingWindowMetrics {

    private final Duration windowSize;
    private final Deque<TimeBucket> buckets = new ArrayDeque<>();

    public SlidingWindowMetrics(Duration windowSize) {
        this.windowSize = windowSize;
    }

    public synchronized void recordSuccess() {
        currentBucket().recordSuccess();
        evictOldBuckets();
    }

    public synchronized void recordFailure() {
        currentBucket().recordFailure();
        evictOldBuckets();
    }

    public synchronized int totalSuccess() {
        evictOldBuckets();
        return buckets.stream().mapToInt(TimeBucket::getSuccess).sum();
    }

    public synchronized int totalFailure() {
        evictOldBuckets();
        return buckets.stream().mapToInt(TimeBucket::getFailure).sum();
    }

    public synchronized int totalRequests() {
        return totalSuccess() + totalFailure();
    }

    public synchronized void reset() {
        buckets.clear();
    }

    /* ----------------- internal helpers ----------------- */

    private TimeBucket currentBucket() {
        Instant now = Instant.now();

        if (buckets.isEmpty() ||
                Duration.between(
                        buckets.peekLast().getTimestamp(), now).getSeconds() >= 1) {

            buckets.addLast(new TimeBucket(now));
        }

        return buckets.peekLast();
    }

    private void evictOldBuckets() {
        Instant now = Instant.now();

        while (!buckets.isEmpty() &&
                Duration.between(
                                buckets.peekFirst().getTimestamp(), now)
                        .compareTo(windowSize) > 0) {

            buckets.removeFirst();
        }
    }
}
