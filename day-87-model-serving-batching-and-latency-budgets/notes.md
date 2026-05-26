# Day 87 - Model Serving: Batching and Latency Budgets

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-26 | **Code:** [`ServingBatchBudget.java`](./ServingBatchBudget.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Split the SLO into a **latency budget** (network + queue + batch wait + inference + post-processing). With cost `fixed + perItem*batch`, the largest batch within budget maximises throughput. Add warm-up, timeouts, shadow/canary rollouts, and model versioning.

## 2. Memory Architecture Breakdown

Per-request overhead is amortised across the batch.

## 3. Interview Edge Cases & Pitfalls

1. Batch wait adds latency.
2. Cold starts break p99 - warm up.

## 4. Interview Q&A

**Q1. How big a batch?**  
Largest batch that still meets the p99 budget.

**Q2. Canary vs shadow?**  
Canary serves a slice of real traffic; shadow mirrors without serving.

## 5. Run the Lab

```bash
cd day-87-model-serving-batching-and-latency-budgets
javac ServingBatchBudget.java
java ServingBatchBudget
```
The program prints benchmark tables and finishes with `Day 87 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
