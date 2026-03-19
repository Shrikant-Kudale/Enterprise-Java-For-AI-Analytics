# Day 19 - Spring Boot 3 Microservices Architecture

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-19 | **Code:** [`MiniBootMicroservice.java`](./MiniBootMicroservice.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Spring Boot** = auto-configuration + embedded server + starters. Core idea: an **IoC container** builds singleton beans and injects constructor dependencies; annotations (`@RestController`, `@GetMapping`) are read by reflection at startup.
* Layers: controller (HTTP mapping) -> service (use case) -> repository (adapter). Keep controllers thin, map exceptions centrally (`@ControllerAdvice`).
* **Microservice essentials**: health/readiness probes (`/actuator/health`), metrics (Micrometer/Prometheus), config via env, graceful shutdown, timeouts on every outbound call, idempotent handlers, correlation ids.
* Spring Boot 3: Java 17+, Jakarta namespace, native images, `spring.threads.virtual.enabled=true` for virtual-thread request handling.
* Today's lab implements the mechanism - DI container with cycle detection + annotation-driven router on `com.sun.net.httpserver` - using only the JDK.

