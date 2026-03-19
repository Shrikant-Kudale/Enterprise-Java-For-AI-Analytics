# Day 19 - Spring Boot 3 Microservices Architecture

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-19 | **Code:** [`MiniBootMicroservice.java`](./MiniBootMicroservice.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Spring Boot** = auto-configuration + embedded server + starters. Core idea: an **IoC container** builds singleton beans and injects constructor dependencies; annotations (`@RestController`, `@GetMapping`) are read by reflection at startup.
* Layers: controller (HTTP mapping) -> service (use case) -> repository (adapter). Keep controllers thin, map exceptions centrally (`@ControllerAdvice`).
* **Microservice essentials**: health/readiness probes (`/actuator/health`), metrics (Micrometer/Prometheus), config via env, graceful shutdown, timeouts on every outbound call, idempotent handlers, correlation ids.
* Spring Boot 3: Java 17+, Jakarta namespace, native images, `spring.threads.virtual.enabled=true` for virtual-thread request handling.
* Today's lab implements the mechanism - DI container with cycle detection + annotation-driven router on `com.sun.net.httpserver` - using only the JDK.

## 2. Memory Architecture Breakdown

```
Container: Map<Class, Object> singletons; create(): pick constructor -> resolve params recursively -> newInstance -> cache
Cycle detection: "currently creating" stack; seeing a class twice => circular dependency error
Request: HttpServer -> virtual thread -> route table lookup (path -> bean + Method) -> reflection invoke -> JSON
```
Reflection `Method.invoke` is JIT-optimised after warm-up; Spring caches handler methods for the same reason.

## 3. Interview Edge Cases & Pitfalls

1. Field injection hides dependencies and blocks immutability - use constructor injection.
2. Circular dependencies indicate a design smell (Boot 2.6+ forbids by default).
3. `@Transactional` is proxy-based: self-invocation bypasses it.
4. Always set connect/read timeouts and a circuit breaker on outbound clients.

