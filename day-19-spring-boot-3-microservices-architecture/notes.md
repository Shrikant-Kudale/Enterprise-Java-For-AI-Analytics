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

## 4. Interview Q&A

**Q1. How does Spring Boot auto-configuration work?**  
`@EnableAutoConfiguration` loads candidate config classes from `AutoConfiguration.imports`, each guarded by `@Conditional*` (class present, bean missing, property set).

**Q2. Singleton bean thread-safety?**  
Singletons are shared across requests, so they must be stateless or internally thread-safe.

**Q3. How do you make a service ready for Kubernetes?**  
Liveness/readiness probes, graceful shutdown, externalised config, resource limits aligned with JVM flags, structured logs, metrics.

## 5. Production Mapping (real libraries)

```xml
<parent><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-parent</artifactId><version>3.3.5</version></parent>
<dependencies>
  <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-web</artifactId></dependency>
  <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>
</dependencies>
```
```java
@RestController
class PredictionController {
    private final PredictionService svc;
    PredictionController(PredictionService svc) { this.svc = svc; }
    @GetMapping("/predict") Map<String,Object> predict(@RequestParam String model, @RequestParam double[] x) {
        return Map.of("model", model, "score", svc.predict(model, x));
    }
}
```
`application.yml`: `spring.threads.virtual.enabled: true`, `management.endpoints.web.exposure.include: health,prometheus`, `server.shutdown: graceful`.

## 6. Run the Lab

```bash
cd day-19-spring-boot-3-microservices-architecture
javac MiniBootMicroservice.java
java MiniBootMicroservice
```
The program prints benchmark tables and finishes with `Day 19 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 7. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
