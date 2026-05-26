# Day 87 - Model Serving: Batching and Latency Budgets

> **Phase:** Machine Learning Foundations & Serving | **Date:** 2026-05-26 | **Code:** [`ServingBatchBudget.java`](./ServingBatchBudget.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Split the SLO into a **latency budget** (network + queue + batch wait + inference + post-processing). With cost `fixed + perItem*batch`, the largest batch within budget maximises throughput. Add warm-up, timeouts, shadow/canary rollouts, and model versioning.

## 2. Memory Architecture Breakdown

Per-request overhead is amortised across the batch.

