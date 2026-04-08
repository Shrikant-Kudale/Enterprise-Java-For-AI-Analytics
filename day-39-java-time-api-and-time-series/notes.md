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

## 5. Run the Lab

```bash
cd day-39-java-time-api-and-time-series
javac JavaTimeSeries.java
java JavaTimeSeries
```
The program prints benchmark tables and finishes with `Day 39 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
