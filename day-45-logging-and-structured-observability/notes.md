# Day 45 - Logging and Structured Observability

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-14 | **Code:** [`StructuredLogging.java`](./StructuredLogging.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Log JSON events with stable keys (level, msg, traceId, latency_ms) so they are searchable. Use SLF4J + Logback/Log4j2 with MDC for correlation ids; log at the boundary once; never log secrets/PII.

