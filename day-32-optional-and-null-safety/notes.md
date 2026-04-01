# Day 32 - Optional and Null Safety

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-01 | **Code:** [`OptionalNullSafety.java`](./OptionalNullSafety.java) | **Runtime:** JDK 21+

## 1. Core Concepts

`Optional<T>` models *absence* in return types (not fields/params). Use `map/flatMap/filter/orElseGet/or`. Never call `get()` blindly.

## 2. Memory Architecture Breakdown

Optional is a heap object (16 B) - avoid in hot loops; `OptionalInt` avoids boxing.

## 3. Interview Edge Cases & Pitfalls

1. `orElse` evaluates its argument eagerly.
2. Do not serialise Optional fields.

## 4. Interview Q&A

**Q1. Optional vs null?**  
Optional forces callers to handle absence in the type system.

**Q2. orElse vs orElseGet?**  
orElseGet is lazy.

## 5. Run the Lab

```bash
cd day-32-optional-and-null-safety
javac OptionalNullSafety.java
java OptionalNullSafety
```
The program prints benchmark tables and finishes with `Day 32 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
