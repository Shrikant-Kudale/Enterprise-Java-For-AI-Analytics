# Day 35 - Enums and State Machines

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-04 | **Code:** [`EnumStateMachine.java`](./EnumStateMachine.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Enums are type-safe singletons that can carry fields and per-constant behaviour. Use `EnumMap/EnumSet` (array-backed, fastest) and enums for job/model lifecycle states.

## 2. Memory Architecture Breakdown

`EnumMap` = array indexed by ordinal; `EnumSet` = a bit vector.

## 3. Interview Edge Cases & Pitfalls

1. Never rely on `ordinal()` for persistence.
2. `valueOf` throws on unknown names.

