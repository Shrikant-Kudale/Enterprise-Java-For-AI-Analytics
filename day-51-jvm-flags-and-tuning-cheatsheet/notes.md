# Day 51 - JVM Flags and Heap Sizing

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-20 | **Code:** [`HeapSizingMath.java`](./HeapSizingMath.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Key flags: `-Xms/-Xmx`, `-XX:MaxRAMPercentage`, `-XX:+UseG1GC|UseZGC`, `-XX:MaxGCPauseMillis`, `-Xlog:gc*`, `-XX:MaxMetaspaceSize`, `-XX:MaxDirectMemorySize`. Budget: heap + metaspace + direct + thread stacks + code cache < container limit.

## 2. Memory Architecture Breakdown

RSS is larger than -Xmx: add ~25-40% headroom.

## 3. Interview Edge Cases & Pitfalls

1. Heap = 100% of container -> OOM-kill.
2. Set Xms=Xmx in prod to avoid resize pauses.

## 4. Interview Q&A

**Q1. How to size heap in Kubernetes?**  
MaxRAMPercentage ~65-75% and verify total RSS.

**Q2. Why Xms = Xmx?**  
Avoids heap resize and commit costs.

## 5. Run the Lab

```bash
cd day-51-jvm-flags-and-tuning-cheatsheet
javac HeapSizingMath.java
java HeapSizingMath
```
The program prints benchmark tables and finishes with `Day 51 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
