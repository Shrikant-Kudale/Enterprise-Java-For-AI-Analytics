# Day 23 - Spring Batch for Massive Dataset Processing

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-23 | **Code:** [`ChunkBatchEngine.java`](./ChunkBatchEngine.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Chunk-oriented processing**: `ItemReader` -> `ItemProcessor` -> `ItemWriter`; read N items, process each, write the chunk in **one transaction**, then checkpoint.
* **Fault tolerance**: *skip* bad records (limit), *retry* transient errors, **restart** from the last committed offset (JobRepository stores step execution context).
* **Scaling**: partitioning (split the key range across workers), multi-threaded steps, remote chunking.
* Chunk size trades commit overhead vs memory and rollback cost; 100-10,000 is typical.
* Make writers **idempotent** (upsert) because a crash can replay the last uncommitted chunk.

## 2. Memory Architecture Breakdown

Only one chunk lives in memory: streaming reader (cursor/paging) + List<O> buffer. Memory is O(chunk), not O(dataset).

## 3. Interview Edge Cases & Pitfalls

1. Reader must be stateless per partition or thread-safe.
2. Skip limits hide data-quality problems - alert on skip count.
3. Large chunk + failure = big rollback.

## 4. Interview Q&A

**Q1. How does restart work?**  
After each commit the offset is persisted; a restarted job reopens the reader at that offset.

**Q2. Chunk size 1 vs 10,000?**  
1 = commit per item (slow); 10,000 = fewer commits but more memory and bigger replay on failure.

