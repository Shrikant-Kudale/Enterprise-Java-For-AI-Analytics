# Day 63 - Transactions and Isolation Levels

> **Phase:** Backend Systems & Security | **Date:** 2026-05-02 | **Code:** [`TransactionIsolationToy.java`](./TransactionIsolationToy.java) | **Runtime:** JDK 21+

## 1. Core Concepts

ACID. Isolation levels: READ UNCOMMITTED, READ COMMITTED (no dirty reads), REPEATABLE READ (no non-repeatable reads), SERIALIZABLE (no phantoms). MVCC gives each transaction a snapshot. Keep transactions short; set `readOnly=true` for reads.

## 2. Memory Architecture Breakdown

MVCC keeps row versions (undo/xmin-xmax) until no snapshot needs them.

## 3. Interview Edge Cases & Pitfalls

1. Lost update without locking/versioning.
2. Long transactions bloat MVCC and block vacuum.

## 4. Interview Q&A

**Q1. Dirty vs non-repeatable read?**  
Dirty reads uncommitted data; non-repeatable read sees a row change between two reads.

**Q2. Default in Postgres/MySQL?**  
READ COMMITTED / REPEATABLE READ.

