# Day 44 - CSV Streaming and Large Files

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-13 | **Code:** [`CsvStreaming.java`](./CsvStreaming.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Stream line by line (`BufferedReader.lines()`, `Files.lines`) so memory is O(1). Parse into primitives, skip header, handle quotes with a real CSV lib for production. Never `readAllLines` a huge file.

## 2. Memory Architecture Breakdown

`Files.lines` holds an open file handle - close it (try-with-resources).

