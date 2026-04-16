# Day 47 - JMH-style Benchmarking Pitfalls

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-16 | **Code:** [`BenchmarkPitfalls.java`](./BenchmarkPitfalls.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Microbenchmarks lie without **warm-up**, dead-code protection (consume results), fixed inputs and multiple forks. Use **JMH** (`@Benchmark`, `Blackhole`). Report ops/s with error bars, compare against a baseline.

## 2. Memory Architecture Breakdown

JIT compiles hot code after thousands of calls; GC and CPU frequency add noise.

## 3. Interview Edge Cases & Pitfalls

1. Constant folding removes the work.
2. Measuring `System.nanoTime` once includes warm-up.

## 4. Interview Q&A

**Q1. Why use JMH?**  
It handles warm-up, forks, dead-code elimination and statistics.

**Q2. What is dead-code elimination?**  
JIT removes computations whose result is unused.

## 5. Run the Lab

```bash
cd day-47-jmh-style-benchmarking-pitfalls
javac BenchmarkPitfalls.java
java BenchmarkPitfalls
```
The program prints benchmark tables and finishes with `Day 47 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
