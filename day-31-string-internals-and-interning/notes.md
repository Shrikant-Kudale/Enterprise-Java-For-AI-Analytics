# Day 31 - String Internals and Interning

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-03-31 | **Code:** [`StringInternals.java`](./StringInternals.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Strings are immutable; the JVM keeps a **string pool** (`intern()`), and since Java 9 uses *compact strings* (Latin-1 `byte[]` when possible). Use `StringBuilder` for loops; `equals`, never `==`.

