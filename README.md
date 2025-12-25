# Aegis — Resilient Outbound Dependency Protection Engine

Aegis is a backend resilience system built using Spring Boot to protect services from cascading failures caused by unhealthy downstream dependencies.

It observes outbound call behavior, tracks recent failures using time-based algorithms, and enforces fail-fast decisions through a deterministic circuit breaker state machine.

---

## Overview

For every outbound HTTP call, Aegis:

- Intercepts dependency calls before network execution
- Evaluates the health of the target dependency
- Tracks success and failure rates using a sliding time window
- Transitions between CLOSED, OPEN, and HALF_OPEN states
- Blocks calls to unhealthy dependencies proactively
- Allows controlled recovery using probe-based execution
- Produces structured, human-readable state transition logs

---

## Outbound Call Protection Pipeline

Service Logic  
↓  
Aegis Decision Engine  
↓  
Circuit State Evaluation  
↓  
Sliding Window Metrics  
↓  
Circuit Breaker Policy  
↓  
Allow / Block Call  
↓  
WebClient  
↓  
External Dependency  

Aegis executes before the actual network call, ensuring unhealthy dependencies are isolated before they degrade the service.

---

## Core Components

### 1. Circuit State Machine

Aegis uses an explicit state machine to control behavior.

States:
- **CLOSED** — Normal operation, calls allowed and observed
- **OPEN** — Dependency deemed unhealthy, calls blocked immediately
- **HALF_OPEN** — Limited probe calls allowed after cooldown

State transitions are time-aware, explicit, and fully observable.

---

### 2. Time-Based Sliding Window Metrics

Aegis tracks only recent behavior instead of lifetime counters.

Characteristics:
- Fixed-duration sliding window (e.g. last 10 seconds)
- Per-second time buckets
- Automatic eviction of old buckets
- Thread-safe aggregation

Metrics tracked:
- Success count
- Failure count
- Total request volume

This enables rate-based failure detection instead of naive totals.

---

### 3. Circuit Breaker Policy

Policy logic is isolated from metrics and state handling.

Current policy evaluates:
- Minimum request threshold
- Failure count within the sliding window
- Cooldown duration before recovery attempts

This separation allows tuning behavior without changing core logic.

---

### 4. HALF_OPEN Controlled Recovery

After a circuit opens:
- A cooldown period is enforced
- Circuit transitions to HALF_OPEN
- Metrics window is reset
- Limited probe traffic is allowed

Outcomes:
- Success → circuit closes immediately
- Failure → circuit reopens immediately

This prevents retry storms and ensures safe recovery.

---

### 5. Asynchronous HTTP Protection

Aegis integrates with Spring WebClient to protect real outbound calls.

Key behaviors:
- Fail-fast before network I/O when circuit is OPEN
- Observes real async outcomes using success/failure hooks
- Handles in-flight requests correctly
- Treats network, DNS, and TLS failures as health signals

---

## Example Logs ```

AEGIS TRANSITION: CLOSED → OPEN | failures=10 totalRequests=10
AEGIS BLOCKED request (state=OPEN)
AEGIS TRANSITION: OPEN → HALF_OPEN (cooldown elapsed)
AEGIS TRANSITION: HALF_OPEN → CLOSED (probe success)
```

These logs make every circuit decision explainable and debuggable.

---

## Design Principles

- Fail-fast over fail-slow
- Rate-based detection over static thresholds
- Time-aware decision making
- Explicit state transitions
- Observability-first design
- Clean separation of concerns
- No hidden framework magic

---

## Technology Stack

- Java 21
- Spring Boot
- Spring WebClient
- Reactor (async HTTP)
- Maven
- Concurrent data structures
- No external resilience libraries
- No annotations in core logic

---

## Running the Application
```
mvn spring-boot:run ```


The application starts on:



http://localhost:8080


---

## Testing the Circuit Breaker

Trigger failures:


```
curl http://localhost:8080/external ```


Repeat rapidly to exceed the failure threshold.

Expected behavior:
- Initial failures are observed
- Circuit transitions to OPEN
- Subsequent calls are blocked immediately

---

## Observe Recovery

1. Wait for the configured cooldown period
2. Call the endpoint again

Aegis will:
- Enter HALF_OPEN
- Allow a probe request
- Close or reopen the circuit based on the outcome

---

## Debug Endpoint

Aegis exposes a debug-only endpoint:



GET /aegis/state


Example response:



AEGIS STATE = OPEN


This endpoint is intended only for development and testing.

---

## Possible Extensions

- Per-dependency circuit breakers
- Success threshold in HALF_OPEN
- Jittered cooldowns
- Bulkhead isolation
- Metrics export (Micrometer / Prometheus)
- Distributed breakers using Redis
- Sentinel + Aegis unified defense layer

---

## What This Project Demonstrates

- Distributed systems failure handling
- Circuit breaker internals
- Time-based algorithm design
- Async behavior and in-flight request handling
- Concurrency-safe state machines
- Production-inspired observability
- Real-world backend engineering tradeoffs
