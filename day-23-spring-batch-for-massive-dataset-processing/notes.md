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

## 5. Production Mapping (real libraries)

```java
@Bean Step step(JobRepository repo, PlatformTransactionManager tx, ItemReader<Txn> r, ItemProcessor<Txn,Row> p, ItemWriter<Row> w) {
    return new StepBuilder("load", repo).<Txn,Row>chunk(1000, tx).reader(r).processor(p).writer(w)
        .faultTolerant().skipLimit(100).skip(ParseException.class).build();
}
```

## 6. Run the Lab

```bash
cd day-23-spring-batch-for-massive-dataset-processing
javac ChunkBatchEngine.java
java ChunkBatchEngine
```
The program prints benchmark tables and finishes with `Day 23 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 7. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
