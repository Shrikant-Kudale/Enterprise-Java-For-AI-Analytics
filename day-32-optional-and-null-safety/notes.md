# Day 32 - Optional and Null Safety

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-01 | **Code:** [`OptionalNullSafety.java`](./OptionalNullSafety.java) | **Runtime:** JDK 21+

## 1. Core Concepts

`Optional<T>` models *absence* in return types (not fields/params). Use `map/flatMap/filter/orElseGet/or`. Never call `get()` blindly.

## 2. Memory Architecture Breakdown

Optional is a heap object (16 B) - avoid in hot loops; `OptionalInt` avoids boxing.

## 3. Interview Edge Cases & Pitfalls

1. `orElse` evaluates its argument eagerly.
2. Do not serialise Optional fields.

