# Day 69 - Consistent Hashing and Sharding

> **Phase:** Backend Systems & Security | **Date:** 2026-05-08 | **Code:** [`ConsistentHashRing.java`](./ConsistentHashRing.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Hash keys and nodes onto a ring; a key belongs to the next node clockwise. Adding/removing a node remaps only ~1/N of keys. **Virtual nodes** smooth the load. Used by caches, Dynamo, Cassandra.

