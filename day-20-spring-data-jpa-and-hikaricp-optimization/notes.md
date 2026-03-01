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

## 4. Interview Q&A

**Q1. How do you detect N+1 in production?**  
Log/count statements per request (Hibernate statistics, datasource-proxy, APM traces); alert on queries per request.

**Q2. Why not set pool size to 100?**  
The DB can only run ~cores*2 queries efficiently; extra connections queue inside the DB, increasing latency.

**Q3. IDENTITY vs SEQUENCE?**  
IDENTITY needs the insert to get the id, which defeats JDBC batching; SEQUENCE with allocationSize pre-fetches ids.

## 5. Production Mapping (real libraries)

```java
@Entity class Customer { @Id Long id; @OneToMany(mappedBy="customer") @BatchSize(size = 50) List<Order> orders; }
interface CustomerRepo extends JpaRepository<Customer, Long> {
    @EntityGraph(attributePaths = "orders") List<Customer> findTop100ByOrderByIdAsc();
    @Query("select new com.acme.CustomerView(c.id, c.name) from Customer c") Slice<CustomerView> view(Pageable p);
}
```
```yaml
spring.datasource.hikari: { maximum-pool-size: 10, minimum-idle: 10, connection-timeout: 2000, max-lifetime: 1500000, leak-detection-threshold: 20000 }
spring.jpa.open-in-view: false
spring.jpa.properties.hibernate: { jdbc.batch_size: 50, order_inserts: true, order_updates: true }
```

## 6. Run the Lab

```bash
cd day-20-spring-data-jpa-and-hikaricp-optimization
javac PoolAndJpaPatterns.java
java PoolAndJpaPatterns
```
The program prints benchmark tables and finishes with `Day 20 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 7. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
