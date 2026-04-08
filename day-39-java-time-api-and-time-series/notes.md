# Day 39 - java.time API and Time Series

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-08 | **Code:** [`JavaTimeSeries.java`](./JavaTimeSeries.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Use `Instant` (UTC machine time), `ZonedDateTime` (human time), `LocalDate`, `Duration`/`Period`. Store UTC, convert at the edge. Immutable and thread-safe (unlike `Date`/`SimpleDateFormat`).

