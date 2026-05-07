# Day 68 - Rate Limiting Algorithms

> **Phase:** Backend Systems & Security | **Date:** 2026-05-07 | **Code:** [`TokenBucketLimiter.java`](./TokenBucketLimiter.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Fixed window (cheap, bursty at edges), sliding window log/counter (accurate), **token bucket** (allows bursts, smooth refill), leaky bucket (constant outflow). Distributed limiters use Redis (INCR+EXPIRE, Lua scripts).

## 2. Memory Architecture Breakdown

Token bucket = two numbers (tokens, lastRefill) per key.

## 3. Interview Edge Cases & Pitfalls

1. Clock skew across nodes.
2. Return 429 with `Retry-After`.

## 4. Interview Q&A

**Q1. Token vs leaky bucket?**  
Token allows bursts up to capacity; leaky enforces a steady rate.

**Q2. Per-user limits at scale?**  
Shard counters in Redis keyed by user.

## 5. Run the Lab

```bash
cd day-68-rate-limiting-algorithms
javac TokenBucketLimiter.java
java TokenBucketLimiter
```
The program prints benchmark tables and finishes with `Day 68 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
