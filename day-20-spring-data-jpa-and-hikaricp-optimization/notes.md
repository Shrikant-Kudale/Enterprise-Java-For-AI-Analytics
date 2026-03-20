# Day 20 - Spring Data JPA and HikariCP Optimization

> **Phase:** Enterprise Backend Services | **Date:** 2026-03-20 | **Code:** [`PoolAndJpaPatterns.java`](./PoolAndJpaPatterns.java) | **Runtime:** JDK 21+

## 1. Core Concepts

* **N+1 problem**: 1 query loads N parents, then N lazy queries load children. Fix with `join fetch`, `@EntityGraph`, `@BatchSize`, or a single `IN (...)` query. Each round trip costs network latency, so count queries not rows.
* **JDBC batching**: send many INSERTs in one round trip (`hibernate.jdbc.batch_size=50`, `order_inserts=true`). `GenerationType.IDENTITY` disables batching - use a SEQUENCE.
* **Connection pool (HikariCP)**: fixed-size pool, `connectionTimeout`, `maxLifetime`, leak detection. A *small* pool wins: `connections ~ cores*2 + spindles`, or Little's law `QPS x avg DB time`.
* Pagination: keyset (`WHERE id > ?`) beats `OFFSET`; `open-in-view=false`; `readOnly=true` transactions; projections (DTO) instead of entities for reads.
* Today: fake DB with per-round-trip latency + a Hikari-style pool built from `ArrayBlockingQueue`.

## 2. Memory Architecture Breakdown

```
Pool: ArrayBlockingQueue<Connection> idle (size = max). borrow() = poll(timeout); close() = offer back (idempotent).
Hibernate: first-level cache (Session) = Map<id, entity> + dirty-check snapshots -> memory grows with loaded entities; clear()/flush() in batch jobs.
```
Throughput rises with pool size only until the DB saturates; more connections after that only add context switching and lock contention.

## 3. Interview Edge Cases & Pitfalls

1. `LazyInitializationException` outside a transaction - fetch what you need or use DTO projections.
2. Pool larger than DB cores often *reduces* throughput.
3. `findAll()` + stream in memory = hidden full table scan.
4. Forgetting `close()` leaks a connection; pool exhaustion shows as timeouts under load.

