# Day 36 - Interfaces, Default Methods and Mixins

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-05 | **Code:** [`DefaultMethodsMixins.java`](./DefaultMethodsMixins.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Default methods let interfaces evolve and act as mixins (behaviour without state). Conflicts must be resolved explicitly with `Iface.super.method()`.

## 2. Memory Architecture Breakdown

Interface call sites are `invokeinterface`; JIT devirtualises monomorphic sites.

## 3. Interview Edge Cases & Pitfalls

1. Diamond conflict is a compile error until resolved.
2. Interfaces can't hold instance state.

## 4. Interview Q&A

**Q1. Abstract class vs interface?**  
Abstract class holds state and single inheritance; interfaces give multiple inheritance of type/behaviour.

**Q2. Why default methods?**  
Backward-compatible API evolution (e.g. `Collection.stream`).

## 5. Run the Lab

```bash
cd day-36-interfaces-default-methods-and-mixins
javac DefaultMethodsMixins.java
java DefaultMethodsMixins
```
The program prints benchmark tables and finishes with `Day 36 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
