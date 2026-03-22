# Day 22 - Redis Caching for High Throughput

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-22 | **Code:** [`CacheAndResp.java`](./CacheAndResp.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Cache-aside**: read cache -> on miss load from DB -> populate with TTL. Writes invalidate (or write-through).
* **Eviction**: LRU/LFU (`maxmemory-policy allkeys-lru`); TTL with jitter avoids synchronized expiry.
* **Stampede / dog-pile**: many threads miss the same hot key at once. Fix with single-flight (one loader, others wait), locks, or stale-while-revalidate.
* **Negative caching** for "not found"; keep TTL short.
* **Redis**: single-threaded command execution, RESP protocol over TCP, pipelining, data types (string, hash, zset, HLL, streams). Use Lettuce/Jedis in Java.
* Today: TTL+LRU cache with an injectable clock, single-flight, RESP encoder/decoder, Zipf hit-ratio experiment.

