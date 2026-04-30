# Day 61 - Intervals and Sweep Line

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-30 | **Code:** [`IntervalsSweepLine.java`](./IntervalsSweepLine.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Sort intervals by start, then merge in one pass. **Sweep line**: turn intervals into +1/-1 events, sort, and track the running count (meeting rooms, max concurrency, overlapping sessions).

## 2. Memory Architecture Breakdown

O(n log n) for sorting, O(n) extra.

