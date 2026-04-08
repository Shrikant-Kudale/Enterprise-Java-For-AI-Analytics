# Day 39 - java.time API and Time Series

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-08 | **Code:** [`JavaTimeSeries.java`](./JavaTimeSeries.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Use `Instant` (UTC machine time), `ZonedDateTime` (human time), `LocalDate`, `Duration`/`Period`. Store UTC, convert at the edge. Immutable and thread-safe (unlike `Date`/`SimpleDateFormat`).

## 2. Memory Architecture Breakdown

`Instant` = seconds + nanos (12 bytes of state).

## 3. Interview Edge Cases & Pitfalls

1. DST gaps/overlaps for local times.
2. Always specify the ZoneId.

## 4. Interview Q&A

**Q1. Why store UTC?**  
No ambiguity across zones/DST.

**Q2. Instant vs LocalDateTime?**  
Instant is a point on the timeline; LocalDateTime has no zone.

