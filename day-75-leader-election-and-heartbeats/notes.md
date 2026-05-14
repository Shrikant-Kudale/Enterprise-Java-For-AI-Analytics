# Day 75 - Leader Election and Heartbeats

> **Phase:** Backend Systems & Security | **Date:** 2026-05-14 | **Code:** [`HeartbeatLeaderElection.java`](./HeartbeatLeaderElection.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Distributed systems elect a leader (Raft, ZooKeeper/etcd leases). Nodes send **heartbeats**; a missing heartbeat past a timeout marks a node dead. Fencing tokens stop a stale leader from writing. Use a library (etcd, Curator) - don't hand-roll consensus.

## 2. Memory Architecture Breakdown

State = term/epoch + lease expiry timestamps.

