# Day 66 - Message Queues and Consumer Groups

> **Phase:** Backend Systems & Security | **Date:** 2026-05-05 | **Code:** [`PartitionedLogToy.java`](./PartitionedLogToy.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Kafka-style log: topics split into **partitions**; messages with the same key hash to the same partition (per-key ordering). A **consumer group** assigns each partition to one consumer; **offsets** record progress. More partitions = more parallelism.

## 2. Memory Architecture Breakdown

Append-only segment files; sequential I/O + page cache make it fast.

## 3. Interview Edge Cases & Pitfalls

1. Ordering is per partition only.
2. Rebalances pause consumption.

## 4. Interview Q&A

**Q1. How do you scale consumers?**  
Add consumers up to the partition count.

**Q2. At-least-once?**  
Commit offsets after processing.

