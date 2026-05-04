# Day 65 - Idempotency and Exactly-Once Semantics

> **Phase:** Backend Systems & Security | **Date:** 2026-05-04 | **Code:** [`IdempotencyKeys.java`](./IdempotencyKeys.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Networks give *at-least-once* delivery. Make handlers **idempotent**: client sends an `Idempotency-Key`; server stores the first result and returns it for repeats. 'Exactly-once' = at-least-once delivery + idempotent processing (or transactional outbox/dedup table).

## 2. Memory Architecture Breakdown

Dedup store needs atomic insert and a TTL.

## 3. Interview Edge Cases & Pitfalls

1. Store the *response*, not only the key.
2. Key scope must include the tenant/user.

## 4. Interview Q&A

**Q1. Exactly-once in Kafka?**  
Idempotent producer + transactions + consumer offsets in the same transaction.

**Q2. Why dedup tables?**  
Convert retries into no-ops.

## 5. Run the Lab

```bash
cd day-65-idempotency-and-exactly-once-semantics
javac IdempotencyKeys.java
java IdempotencyKeys
```
The program prints benchmark tables and finishes with `Day 65 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
