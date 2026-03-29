# Day 29 - Enterprise System Design: AI Microservice

> **Phase:** System Design & Interview Capstone | **Date:** 2026-03-29 | **Code:** [`ResilientGateway.java`](./ResilientGateway.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Design flow**: requirements (QPS, p99, accuracy, cost) -> API -> capacity estimate -> components -> failure modes -> scaling -> monitoring.
* **Inference gateway** pipeline: auth -> **rate limit** (token bucket) -> **cache** -> **bulkhead** (bound concurrency) -> **circuit breaker** -> **retry with backoff** -> **fallback** (cached/default model).
* **Capacity**: in-flight = QPS x latency (Little's law); replicas = in-flight / per-replica concurrency / target utilisation (~60%).
* Scale-out: stateless replicas behind an LB, async queue for batch jobs, model registry + canary rollout, feature store, observability (RED metrics, traces).

