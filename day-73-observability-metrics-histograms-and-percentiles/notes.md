# Day 73 - Metrics, Histograms and Percentiles

> **Phase:** Backend Systems & Security | **Date:** 2026-05-12 | **Code:** [`LatencyPercentiles.java`](./LatencyPercentiles.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Averages hide tail latency. Track **p50/p95/p99** with histograms (HdrHistogram, Micrometer timers). Follow RED (Rate, Errors, Duration) for services and USE (Utilisation, Saturation, Errors) for resources. Alert on SLO burn rate.

## 2. Memory Architecture Breakdown

Histograms use fixed buckets - O(1) record, tiny memory.

## 3. Interview Edge Cases & Pitfalls

1. Averaging percentiles across hosts is wrong - merge histograms.
2. Coordinated omission under-reports latency.

## 4. Interview Q&A

**Q1. Why p99?**  
Users experience the tail; fan-out amplifies it.

**Q2. SLI/SLO?**  
Measured indicator vs target.

## 5. Run the Lab

```bash
cd day-73-observability-metrics-histograms-and-percentiles
javac LatencyPercentiles.java
java LatencyPercentiles
```
The program prints benchmark tables and finishes with `Day 73 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
