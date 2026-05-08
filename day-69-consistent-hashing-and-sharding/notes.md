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

