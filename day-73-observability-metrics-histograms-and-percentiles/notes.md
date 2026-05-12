# Day 73 - Metrics, Histograms and Percentiles

> **Phase:** Backend Systems & Security | **Date:** 2026-05-12 | **Code:** [`LatencyPercentiles.java`](./LatencyPercentiles.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Averages hide tail latency. Track **p50/p95/p99** with histograms (HdrHistogram, Micrometer timers). Follow RED (Rate, Errors, Duration) for services and USE (Utilisation, Saturation, Errors) for resources. Alert on SLO burn rate.

## 2. Memory Architecture Breakdown

Histograms use fixed buckets - O(1) record, tiny memory.

