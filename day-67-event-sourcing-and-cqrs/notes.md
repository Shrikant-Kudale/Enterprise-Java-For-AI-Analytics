# Day 67 - Event Sourcing and CQRS

> **Phase:** Backend Systems & Security | **Date:** 2026-05-06 | **Code:** [`EventSourcingReplay.java`](./EventSourcingReplay.java) | **Runtime:** JDK 21+

## 1. Core Concepts

**Event sourcing** stores immutable events; state = fold(events). Gives audit, replay and time travel. **CQRS** separates the write model from read-optimised projections. Snapshots bound replay cost.

## 2. Memory Architecture Breakdown

Append-only store + projections rebuilt from the log.

