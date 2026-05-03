# Day 64 - Optimistic Locking and Versioning

> **Phase:** Backend Systems & Security | **Date:** 2026-05-03 | **Code:** [`OptimisticVersioning.java`](./OptimisticVersioning.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Optimistic locking adds a **version** column: `UPDATE ... WHERE id=? AND version=?`; zero rows updated means a concurrent change -> retry/409. JPA `@Version`. Best when conflicts are rare; use pessimistic locks (`SELECT FOR UPDATE`) when they are frequent.

## 2. Memory Architecture Breakdown

Compare-and-set (CAS) is the in-memory equivalent.

## 3. Interview Edge Cases & Pitfalls

1. Retries must re-read state.
2. ABA problem needs version stamps.

## 4. Interview Q&A

**Q1. Optimistic vs pessimistic?**  
Optimistic detects conflicts at commit; pessimistic blocks up front.

**Q2. HTTP mapping?**  
ETag/If-Match with 412/409.

