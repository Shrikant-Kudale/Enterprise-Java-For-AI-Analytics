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

## 5. Run the Lab

```bash
cd day-66-message-queues-and-consumer-groups
javac PartitionedLogToy.java
java PartitionedLogToy
```
The program prints benchmark tables and finishes with `Day 66 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
