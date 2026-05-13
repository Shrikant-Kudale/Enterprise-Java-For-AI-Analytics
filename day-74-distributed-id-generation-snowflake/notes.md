# Day 74 - Distributed ID Generation (Snowflake)

> **Phase:** Backend Systems & Security | **Date:** 2026-05-13 | **Code:** [`SnowflakeIds.java`](./SnowflakeIds.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Snowflake IDs pack **timestamp | node id | sequence** into 64 bits: roughly time-ordered, unique without coordination, index-friendly. Alternatives: UUIDv7, ULID, DB sequences. Beware clock moving backwards.

## 2. Memory Architecture Breakdown

41 bits time + 10 bits node + 12 bits sequence = 4096 ids/ms/node.

## 3. Interview Edge Cases & Pitfalls

1. Clock rollback can duplicate ids.
2. Random UUIDv4 fragments B-tree indexes.

## 4. Interview Q&A

**Q1. Why time-ordered ids?**  
Better insert locality in indexes.

**Q2. Sequence overflow?**  
Wait for the next millisecond.

## 5. Run the Lab

```bash
cd day-74-distributed-id-generation-snowflake
javac SnowflakeIds.java
java SnowflakeIds
```
The program prints benchmark tables and finishes with `Day 74 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
