# Day 47 - JMH-style Benchmarking Pitfalls

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-16 | **Code:** [`BenchmarkPitfalls.java`](./BenchmarkPitfalls.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Microbenchmarks lie without **warm-up**, dead-code protection (consume results), fixed inputs and multiple forks. Use **JMH** (`@Benchmark`, `Blackhole`). Report ops/s with error bars, compare against a baseline.

## 2. Memory Architecture Breakdown

JIT compiles hot code after thousands of calls; GC and CPU frequency add noise.

