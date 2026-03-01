# Day 35 - Enums and State Machines

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-04 | **Code:** [`EnumStateMachine.java`](./EnumStateMachine.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Enums are type-safe singletons that can carry fields and per-constant behaviour. Use `EnumMap/EnumSet` (array-backed, fastest) and enums for job/model lifecycle states.

## 2. Memory Architecture Breakdown

`EnumMap` = array indexed by ordinal; `EnumSet` = a bit vector.

## 3. Interview Edge Cases & Pitfalls

1. Never rely on `ordinal()` for persistence.
2. `valueOf` throws on unknown names.

## 4. Interview Q&A

**Q1. Enum vs sealed interface?**  
Enum: fixed identical-shape constants; sealed: variants with different data.

**Q2. Why EnumMap?**  
Array lookup, no hashing.

## 5. Run the Lab

```bash
cd day-35-enums-and-state-machines
javac EnumStateMachine.java
java EnumStateMachine
```
The program prints benchmark tables and finishes with `Day 35 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
