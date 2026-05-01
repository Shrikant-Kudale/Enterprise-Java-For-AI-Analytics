# Day 62 - HTTP Client, Timeouts and Retries

> **Phase:** Backend Systems & Security | **Date:** 2026-05-01 | **Code:** [`RetryBackoffClient.java`](./RetryBackoffClient.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Every outbound call needs connect + request timeouts, bounded retries with **exponential backoff + jitter**, only for idempotent operations, plus a circuit breaker. Java 11+ `java.net.http.HttpClient` supports HTTP/2, async and timeouts.

## 2. Memory Architecture Breakdown

A shared `HttpClient` reuses connections; create it once.

## 3. Interview Edge Cases & Pitfalls

1. Retry storms amplify outages.
2. Never retry non-idempotent POSTs without an idempotency key.

