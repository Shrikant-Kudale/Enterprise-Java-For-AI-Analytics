# Day 38 - Reflection and Annotations

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-07 | **Code:** [`ReflectionAnnotations.java`](./ReflectionAnnotations.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Reflection inspects classes at runtime (`getDeclaredMethods`, `Method.invoke`); annotations with `RUNTIME` retention drive frameworks (Spring, JPA). It is slower and bypasses compile-time checks - cache lookups, prefer MethodHandles.

