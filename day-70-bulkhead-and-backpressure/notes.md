# Day 70 - Bulkhead and Backpressure

> **Phase:** Backend Systems & Security | **Date:** 2026-05-09 | **Code:** [`BulkheadBackpressure.java`](./BulkheadBackpressure.java) | **Runtime:** JDK 21+

## 1. Core Concepts

**Bulkheads** isolate resources (separate pools/semaphores per dependency) so one slow service cannot exhaust everything. **Backpressure** pushes load back: bounded queues, `CallerRuns`, reactive demand signals, 429/503 responses.

## 2. Memory Architecture Breakdown

Semaphore = counter; bounded queue = array of fixed capacity.

## 3. Interview Edge Cases & Pitfalls

1. Unbounded queues hide overload.
2. Shed load early, not after queueing.

## 4. Interview Q&A

**Q1. Bulkhead vs circuit breaker?**  
Bulkhead limits concurrency; breaker stops calling a failing dependency.

**Q2. How to apply backpressure?**  
Bound queues and reject/slow producers.

## 5. Run the Lab

```bash
cd day-70-bulkhead-and-backpressure
javac BulkheadBackpressure.java
java BulkheadBackpressure
```
The program prints benchmark tables and finishes with `Day 70 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
