# Day 69 - Consistent Hashing and Sharding

> **Phase:** Backend Systems & Security | **Date:** 2026-05-08 | **Code:** [`ConsistentHashRing.java`](./ConsistentHashRing.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Hash keys and nodes onto a ring; a key belongs to the next node clockwise. Adding/removing a node remaps only ~1/N of keys. **Virtual nodes** smooth the load. Used by caches, Dynamo, Cassandra.

## 2. Memory Architecture Breakdown

A `TreeMap<hash,node>` gives O(log n) lookups via `ceilingEntry`.

## 3. Interview Edge Cases & Pitfalls

1. Hot keys still overload one node.
2. Use a well-mixed hash.

## 4. Interview Q&A

**Q1. Why not `hash % N`?**  
Changing N remaps almost every key.

**Q2. Why virtual nodes?**  
Even distribution and smoother rebalancing.

## 5. Run the Lab

```bash
cd day-69-consistent-hashing-and-sharding
javac ConsistentHashRing.java
java ConsistentHashRing
```
The program prints benchmark tables and finishes with `Day 69 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
