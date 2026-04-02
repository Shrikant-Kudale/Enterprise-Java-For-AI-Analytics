# Day 33 - Immutability and Defensive Copying

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-02 | **Code:** [`ImmutabilityCopies.java`](./ImmutabilityCopies.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Immutable objects are thread-safe and cache-friendly. Make fields `final`, copy mutable inputs/outputs, prefer `List.copyOf`, records and unmodifiable views *plus* copies.

## 2. Memory Architecture Breakdown

`Collections.unmodifiableList` is a *view* (changes show through); `List.copyOf` is an independent immutable snapshot.

