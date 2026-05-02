# Day 63 - Transactions and Isolation Levels

> **Phase:** Backend Systems & Security | **Date:** 2026-05-02 | **Code:** [`TransactionIsolationToy.java`](./TransactionIsolationToy.java) | **Runtime:** JDK 21+

## 1. Core Concepts

ACID. Isolation levels: READ UNCOMMITTED, READ COMMITTED (no dirty reads), REPEATABLE READ (no non-repeatable reads), SERIALIZABLE (no phantoms). MVCC gives each transaction a snapshot. Keep transactions short; set `readOnly=true` for reads.

