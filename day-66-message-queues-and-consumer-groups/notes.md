# Day 66 - Message Queues and Consumer Groups

> **Phase:** Backend Systems & Security | **Date:** 2026-05-05 | **Code:** [`PartitionedLogToy.java`](./PartitionedLogToy.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Kafka-style log: topics split into **partitions**; messages with the same key hash to the same partition (per-key ordering). A **consumer group** assigns each partition to one consumer; **offsets** record progress. More partitions = more parallelism.

