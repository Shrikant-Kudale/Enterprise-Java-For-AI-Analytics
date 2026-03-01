# Day 31 - String Internals and Interning

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-03-31 | **Code:** [`StringInternals.java`](./StringInternals.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Strings are immutable; the JVM keeps a **string pool** (`intern()`), and since Java 9 uses *compact strings* (Latin-1 `byte[]` when possible). Use `StringBuilder` for loops; `equals`, never `==`.

## 2. Memory Architecture Breakdown

A `String` = object header + `byte[] value` + coder + cached hash. `+=` in a loop creates a new array each time.

## 3. Interview Edge Cases & Pitfalls

1. `split` drops trailing empty strings.
2. `==` works only for pooled literals.

## 4. Interview Q&A

**Q1. Why is String immutable?**  
Safe sharing, caching of hash, thread-safety and pooling.

**Q2. When to intern?**  
Rarely - only for many duplicate long-lived strings.

## 5. Run the Lab

```bash
cd day-31-string-internals-and-interning
javac StringInternals.java
java StringInternals
```
The program prints benchmark tables and finishes with `Day 31 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
