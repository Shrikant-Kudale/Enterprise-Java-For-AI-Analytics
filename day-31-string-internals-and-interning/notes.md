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

