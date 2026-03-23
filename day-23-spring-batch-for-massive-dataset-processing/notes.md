# Day 23 - Spring Batch for Massive Dataset Processing

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-23 | **Code:** [`ChunkBatchEngine.java`](./ChunkBatchEngine.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Chunk-oriented processing**: `ItemReader` -> `ItemProcessor` -> `ItemWriter`; read N items, process each, write the chunk in **one transaction**, then checkpoint.
* **Fault tolerance**: *skip* bad records (limit), *retry* transient errors, **restart** from the last committed offset (JobRepository stores step execution context).
* **Scaling**: partitioning (split the key range across workers), multi-threaded steps, remote chunking.
* Chunk size trades commit overhead vs memory and rollback cost; 100-10,000 is typical.
* Make writers **idempotent** (upsert) because a crash can replay the last uncommitted chunk.

