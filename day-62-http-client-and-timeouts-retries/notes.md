# Day 62 - HTTP Client, Timeouts and Retries

> **Phase:** Backend Systems & Security | **Date:** 2026-05-01 | **Code:** [`RetryBackoffClient.java`](./RetryBackoffClient.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Every outbound call needs connect + request timeouts, bounded retries with **exponential backoff + jitter**, only for idempotent operations, plus a circuit breaker. Java 11+ `java.net.http.HttpClient` supports HTTP/2, async and timeouts.

## 2. Memory Architecture Breakdown

A shared `HttpClient` reuses connections; create it once.

## 3. Interview Edge Cases & Pitfalls

1. Retry storms amplify outages.
2. Never retry non-idempotent POSTs without an idempotency key.

## 4. Interview Q&A

**Q1. Why jitter?**  
Prevents synchronized retries (thundering herd).

**Q2. What to retry?**  
Timeouts, 502/503/504, connection resets.

## 5. Run the Lab

```bash
cd day-62-http-client-and-timeouts-retries
javac RetryBackoffClient.java
java RetryBackoffClient
```
The program prints benchmark tables and finishes with `Day 62 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
