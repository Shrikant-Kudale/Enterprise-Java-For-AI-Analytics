# Day 51 - JVM Flags and Heap Sizing

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-20 | **Code:** [`HeapSizingMath.java`](./HeapSizingMath.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Key flags: `-Xms/-Xmx`, `-XX:MaxRAMPercentage`, `-XX:+UseG1GC|UseZGC`, `-XX:MaxGCPauseMillis`, `-Xlog:gc*`, `-XX:MaxMetaspaceSize`, `-XX:MaxDirectMemorySize`. Budget: heap + metaspace + direct + thread stacks + code cache < container limit.

## 2. Memory Architecture Breakdown

RSS is larger than -Xmx: add ~25-40% headroom.

