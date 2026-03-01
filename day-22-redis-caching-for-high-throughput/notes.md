# Day 22 - Redis Caching for High Throughput

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-22 | **Code:** [`CacheAndResp.java`](./CacheAndResp.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **Cache-aside**: read cache -> on miss load from DB -> populate with TTL. Writes invalidate (or write-through).
* **Eviction**: LRU/LFU (`maxmemory-policy allkeys-lru`); TTL with jitter avoids synchronized expiry.
* **Stampede / dog-pile**: many threads miss the same hot key at once. Fix with single-flight (one loader, others wait), locks, or stale-while-revalidate.
* **Negative caching** for "not found"; keep TTL short.
* **Redis**: single-threaded command execution, RESP protocol over TCP, pipelining, data types (string, hash, zset, HLL, streams). Use Lettuce/Jedis in Java.
* Today: TTL+LRU cache with an injectable clock, single-flight, RESP encoder/decoder, Zipf hit-ratio experiment.

## 2. Memory Architecture Breakdown

LinkedHashMap(accessOrder=true) keeps an intrusive doubly-linked list: get() moves the entry to the tail, removeEldestEntry evicts the head.
RESP: `*3\r\n$3\r\nSET\r\n$1\r\nk\r\n$1\r\nv\r\n` - arrays of length-prefixed bulk strings.

## 3. Interview Edge Cases & Pitfalls

1. Hot-key skew: one Redis shard can melt - replicate or use local L1 cache.
2. Cache invalidation races: use versioned keys or short TTL.
3. Serialization size dominates network cost; compress large values.

## 4. Interview Q&A

**Q1. How do you prevent a cache stampede?**  
Single-flight per key, probabilistic early refresh, TTL jitter, or serving stale while one thread refreshes.

**Q2. Why is Redis fast?**  
In-memory, single-threaded event loop (no locks), efficient data structures, pipelining.

## 5. Production Mapping (real libraries)

```xml
<dependency><groupId>io.lettuce</groupId><artifactId>lettuce-core</artifactId><version>6.3.2.RELEASE</version></dependency>
```
```java
RedisClient c = RedisClient.create("redis://localhost:6379");
RedisCommands<String,String> r = c.connect().sync();
r.setex("pred:42", 300, "0.91"); String v = r.get("pred:42");
```
Spring: `@Cacheable(cacheNames="pred", key="#id")` + `spring.cache.type=redis`, `spring.cache.redis.time-to-live=5m`.

## 6. Run the Lab

```bash
cd day-22-redis-caching-for-high-throughput
javac CacheAndResp.java
java CacheAndResp
```
The program prints benchmark tables and finishes with `Day 22 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 7. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
