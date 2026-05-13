# Day 74 - Distributed ID Generation (Snowflake)

> **Phase:** Backend Systems & Security | **Date:** 2026-05-13 | **Code:** [`SnowflakeIds.java`](./SnowflakeIds.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Snowflake IDs pack **timestamp | node id | sequence** into 64 bits: roughly time-ordered, unique without coordination, index-friendly. Alternatives: UUIDv7, ULID, DB sequences. Beware clock moving backwards.

