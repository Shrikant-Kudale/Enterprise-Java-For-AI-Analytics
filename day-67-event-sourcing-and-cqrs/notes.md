# Day 67 - Event Sourcing and CQRS

> **Phase:** Backend Systems & Security | **Date:** 2026-05-06 | **Code:** [`EventSourcingReplay.java`](./EventSourcingReplay.java) | **Runtime:** JDK 21+

## 1. Core Concepts

**Event sourcing** stores immutable events; state = fold(events). Gives audit, replay and time travel. **CQRS** separates the write model from read-optimised projections. Snapshots bound replay cost.

## 2. Memory Architecture Breakdown

Append-only store + projections rebuilt from the log.

## 3. Interview Edge Cases & Pitfalls

1. Event schema evolution needs upcasters.
2. Eventual consistency between write and read sides.

## 4. Interview Q&A

**Q1. Why snapshots?**  
Avoid replaying millions of events.

**Q2. CQRS cost?**  
Two models and eventual consistency.

## 5. Run the Lab

```bash
cd day-67-event-sourcing-and-cqrs
javac EventSourcingReplay.java
java EventSourcingReplay
```
The program prints benchmark tables and finishes with `Day 67 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
