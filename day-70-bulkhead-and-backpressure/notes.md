# Day 70 - Bulkhead and Backpressure

> **Phase:** Backend Systems & Security | **Date:** 2026-05-09 | **Code:** [`BulkheadBackpressure.java`](./BulkheadBackpressure.java) | **Runtime:** JDK 21+

## 1. Core Concepts

**Bulkheads** isolate resources (separate pools/semaphores per dependency) so one slow service cannot exhaust everything. **Backpressure** pushes load back: bounded queues, `CallerRuns`, reactive demand signals, 429/503 responses.

## 2. Memory Architecture Breakdown

Semaphore = counter; bounded queue = array of fixed capacity.

