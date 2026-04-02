# Day 33 - Immutability and Defensive Copying

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-02 | **Code:** [`ImmutabilityCopies.java`](./ImmutabilityCopies.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Immutable objects are thread-safe and cache-friendly. Make fields `final`, copy mutable inputs/outputs, prefer `List.copyOf`, records and unmodifiable views *plus* copies.

## 2. Memory Architecture Breakdown

`Collections.unmodifiableList` is a *view* (changes show through); `List.copyOf` is an independent immutable snapshot.

## 3. Interview Edge Cases & Pitfalls

1. Unmodifiable view != immutable.
2. Shallow immutability: contents may still mutate.

## 4. Interview Q&A

**Q1. Why defensive copies?**  
Callers can mutate shared references and break invariants.

**Q2. Cost?**  
Extra allocation; use immutable types to avoid repeating it.

## 5. Run the Lab

```bash
cd day-33-immutability-and-defensive-copying
javac ImmutabilityCopies.java
java ImmutabilityCopies
```
The program prints benchmark tables and finishes with `Day 33 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
