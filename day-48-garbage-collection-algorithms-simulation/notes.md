# Day 48 - Garbage Collection: Mark-Sweep Simulation

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-17 | **Code:** [`MarkSweepSimulation.java`](./MarkSweepSimulation.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Tracing GC: **mark** everything reachable from roots, **sweep** (or copy/compact) the rest. Cycles are collected (unlike ref counting). Generational hypothesis: most objects die young. G1/ZGC add regions and concurrency.

