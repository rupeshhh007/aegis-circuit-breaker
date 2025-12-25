package com.example.aegis.aegis.engine;

import java.time.Duration;

import com.example.aegis.aegis.metrics.SlidingWindowMetrics;
import com.example.aegis.aegis.policy.CircuitBreakerPolicy;
import com.example.aegis.aegis.state.CircuitState;
import com.example.aegis.aegis.state.CircuitStateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AegisEngine {

    private final CircuitStateHolder stateHolder;
    private final SlidingWindowMetrics metrics;
    private final CircuitBreakerPolicy policy;
    private static final Logger log = LoggerFactory.getLogger(AegisEngine.class);

    public AegisEngine(CircuitBreakerPolicy policy) {
        this.stateHolder = new CircuitStateHolder();
        this.metrics = new SlidingWindowMetrics(Duration.ofSeconds(10));
        this.policy = policy;
    }

    public boolean allowRequest() {
        CircuitState state = stateHolder.getState();

        if (state == CircuitState.OPEN &&
                stateHolder.canAttemptReset(policy.cooldown())) {

            log.warn("AEGIS TRANSITION: OPEN → HALF_OPEN (cooldown elapsed)");
            stateHolder.transitionTo(CircuitState.HALF_OPEN);
            metrics.reset();
            return true;
        }

        if (state == CircuitState.OPEN) {
            log.debug("AEGIS BLOCKED request (state=OPEN)");
        }

        return state != CircuitState.OPEN;
    }


    public void recordSuccess() {
        metrics.recordSuccess();

        if (stateHolder.getState() == CircuitState.HALF_OPEN) {
            log.info("AEGIS TRANSITION: HALF_OPEN → CLOSED (probe success)");
            stateHolder.transitionTo(CircuitState.CLOSED);
            metrics.reset();
        }
    }

    public void recordFailure() {
        metrics.recordFailure();

        CircuitState state = stateHolder.getState();

        if (state == CircuitState.HALF_OPEN) {
            log.error("AEGIS TRANSITION: HALF_OPEN → OPEN (probe failure)");
            stateHolder.transitionTo(CircuitState.OPEN);
            return;
        }

        if (state == CircuitState.CLOSED &&
                policy.shouldOpen(
                        metrics.totalFailure(),
                        metrics.totalRequests())) {

            log.error(
                    "AEGIS TRANSITION: CLOSED → OPEN | failures={} totalRequests={}",
                    metrics.totalFailure(),
                    metrics.totalRequests()
            );

            stateHolder.transitionTo(CircuitState.OPEN);
        }
    }
    public void logSnapshot() {
        log.info(
                "AEGIS SNAPSHOT | state={} failures={} successes={} total={}",
                stateHolder.getState(),
                metrics.totalFailure(),
                metrics.totalSuccess(),
                metrics.totalRequests()
        );
    }

    public CircuitState currentState() {
        return stateHolder.getState();
    }
}
