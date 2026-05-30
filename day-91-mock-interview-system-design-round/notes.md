# Day 91 - Mock System Design: Capacity Math

> **Phase:** Mock Interviews & Wrap-up | **Date:** 2026-05-30 | **Code:** [`SystemDesignMath.java`](./SystemDesignMath.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Back-of-envelope: QPS, in-flight = QPS x latency, replicas = in-flight / concurrency / utilisation, storage = items x bytes x replication. State assumptions, draw API -> services -> data stores, then discuss bottlenecks, failure modes, cost and monitoring.

## 2. Memory Architecture Breakdown

Embedding store: 1B items x 768 dims x 4 B = ~3 TB before replication.

## 3. Interview Edge Cases & Pitfalls

1. Peak vs average traffic.
2. Replication and index overhead multiply storage.

## 4. Interview Q&A

**Q1. How to structure the round?**  
Requirements -> estimates -> high-level design -> deep dive -> trade-offs.

**Q2. What to monitor?**  
RED metrics, saturation, model quality/drift.

