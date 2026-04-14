# Day 45 - Logging and Structured Observability

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-14 | **Code:** [`StructuredLogging.java`](./StructuredLogging.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Log JSON events with stable keys (level, msg, traceId, latency_ms) so they are searchable. Use SLF4J + Logback/Log4j2 with MDC for correlation ids; log at the boundary once; never log secrets/PII.

## 2. Memory Architecture Breakdown

Parameterised logging (`log.info("x {}", v)`) avoids string building when the level is off.

## 3. Interview Edge Cases & Pitfalls

1. Logging inside tight loops hurts throughput.
2. Don't log and rethrow.

## 4. Interview Q&A

**Q1. Structured vs plain logs?**  
Machine-parseable fields enable queries and alerts.

**Q2. What is MDC?**  
Thread-local key/values added to every log line.

## 5. Run the Lab

```bash
cd day-45-logging-and-structured-observability
javac StructuredLogging.java
java StructuredLogging
```
The program prints benchmark tables and finishes with `Day 45 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
