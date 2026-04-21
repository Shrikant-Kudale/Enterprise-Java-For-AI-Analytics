# Day 52 - Profiling, JFR and Thread Dumps

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-21 | **Code:** [`ThreadDumpsProfiling.java`](./ThreadDumpsProfiling.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Diagnose with **JFR** (`jcmd <pid> JFR.start`), thread dumps (`jcmd Thread.print`), heap dumps, async-profiler flame graphs. Look for BLOCKED/WAITING threads, hot frames and allocation hot spots. Profile before optimising.

## 2. Memory Architecture Breakdown

JFR has <2% overhead and records GC, allocation, locks and I/O events.

