# Day 75 - Leader Election and Heartbeats

> **Phase:** Backend Systems & Security | **Date:** 2026-05-14 | **Code:** [`HeartbeatLeaderElection.java`](./HeartbeatLeaderElection.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Distributed systems elect a leader (Raft, ZooKeeper/etcd leases). Nodes send **heartbeats**; a missing heartbeat past a timeout marks a node dead. Fencing tokens stop a stale leader from writing. Use a library (etcd, Curator) - don't hand-roll consensus.

## 2. Memory Architecture Breakdown

State = term/epoch + lease expiry timestamps.

## 3. Interview Edge Cases & Pitfalls

1. Clock skew breaks lease logic.
2. Split brain without fencing.

## 4. Interview Q&A

**Q1. Why Raft?**  
Understandable consensus: leader election + log replication.

**Q2. What is a fencing token?**  
Monotonic number so storage rejects old leaders.

## 5. Run the Lab

```bash
cd day-75-leader-election-and-heartbeats
javac HeartbeatLeaderElection.java
java HeartbeatLeaderElection
```
The program prints benchmark tables and finishes with `Day 75 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
