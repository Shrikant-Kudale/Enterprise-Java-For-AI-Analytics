# Day 45 - Logging and Structured Observability

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-14 | **Code:** [`StructuredLogging.java`](./StructuredLogging.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Log JSON events with stable keys (level, msg, traceId, latency_ms) so they are searchable. Use SLF4J + Logback/Log4j2 with MDC for correlation ids; log at the boundary once; never log secrets/PII.

## 2. Memory Architecture Breakdown

Parameterised logging (`log.info("x {}", v)`) avoids string building when the level is off.

## 3. Interview Edge Cases & Pitfalls

1. Logging inside tight loops hurts throughput.
2. Don't log and rethrow.

