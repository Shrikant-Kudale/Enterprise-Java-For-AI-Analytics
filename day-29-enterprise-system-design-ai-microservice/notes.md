# Day 29 - Enterprise System Design: AI Microservice

> **Phase:** System Design & Interview Capstone | **Date:** 2026-03-29 | **Code:** [`ResilientGateway.java`](./ResilientGateway.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Design flow**: requirements (QPS, p99, accuracy, cost) -> API -> capacity estimate -> components -> failure modes -> scaling -> monitoring.
* **Inference gateway** pipeline: auth -> **rate limit** (token bucket) -> **cache** -> **bulkhead** (bound concurrency) -> **circuit breaker** -> **retry with backoff** -> **fallback** (cached/default model).
* **Capacity**: in-flight = QPS x latency (Little's law); replicas = in-flight / per-replica concurrency / target utilisation (~60%).
* Scale-out: stateless replicas behind an LB, async queue for batch jobs, model registry + canary rollout, feature store, observability (RED metrics, traces).

## 2. Memory Architecture Breakdown

Each protection is a small state machine (counter, semaphore, state enum) - O(1) memory. Cache bounded by LRU; breaker state shared per downstream.

## 3. Interview Edge Cases & Pitfalls

1. Retries multiply load - retry budget + jitter, only idempotent calls.
2. Breaker thresholds too low flap; too high delay protection.
3. Cache keys must include model version.

## 4. Interview Q&A

**Q1. How do you size the cluster for 5000 QPS at 40 ms p99?**  
In-flight = 5000 x 0.04 = 200; at 8 concurrent per replica = 25; at 60% utilisation = 42 replicas.

**Q2. Circuit breaker states?**  
CLOSED (normal) -> OPEN after N failures (fail fast) -> HALF_OPEN after a cool-down (trial call) -> CLOSED on success.

## 5. Run the Lab

```bash
cd day-29-enterprise-system-design-ai-microservice
javac ResilientGateway.java
java ResilientGateway
```
The program prints benchmark tables and finishes with `Day 29 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
