# Day 68 - Rate Limiting Algorithms

> **Phase:** Backend Systems & Security | **Date:** 2026-05-07 | **Code:** [`TokenBucketLimiter.java`](./TokenBucketLimiter.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Fixed window (cheap, bursty at edges), sliding window log/counter (accurate), **token bucket** (allows bursts, smooth refill), leaky bucket (constant outflow). Distributed limiters use Redis (INCR+EXPIRE, Lua scripts).

