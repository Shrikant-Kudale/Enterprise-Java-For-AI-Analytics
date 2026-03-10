# Day 10 - Custom Data Structures for Analytics

> **Phase:** Collections & Data Structures | **Date:** 2026-03-10 | **Code:** [`AnalyticsDataStructures.java`](./AnalyticsDataStructures.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Probabilistic / streaming structures
When data is too large for exact answers in memory, trade a **bounded, quantified error** for orders of magnitude less memory and O(1) updates. All are *mergeable* (combine partial results from shards - the property that makes them perfect for distributed analytics).

| Structure | Question answered | Error | Memory |
|-----------|------------------|-------|--------|
| **Bloom filter** | "have I seen x?" | false positives only (never false negatives), rate p | `m = -n ln p / (ln 2)^2` bits, `k = (m/n) ln 2` hashes (~9.6 bits/key at 1%) |
| **Count-Min Sketch** | "how often did x occur?" | over-estimates only; `err <= e*N/w` with prob `1 - e^-d` | `d x w` counters |
| **HyperLogLog** | "how many distinct?" | std error `1.04/sqrt(m)` | `m` 6-bit registers (m=4096 -> ~3 KB, 1.6%) |
| **Reservoir sampling (Algo R)** | uniform sample of K from unknown-length stream | exact uniformity | K items |
| **Welford / Chan** | running mean & variance, mergeable | numerically stable | O(1) |
| t-digest / HDRHistogram | quantiles (p50/p99) | relative | small |

### How they work
* **Bloom**: k hash functions set k bits; lookup checks all k. Double hashing `h_i = h1 + i*h2` gives k hashes from two (Kirsch-Mitzenmacher). Can't delete (use counting Bloom / cuckoo filter).
* **Count-Min**: d rows x w columns; update increments one cell per row (row-specific hash); estimate = **min** across rows because collisions only add.
* **HyperLogLog**: hash x to 64 bits; first p bits choose a register; store the max *rank* (position of first 1-bit) of the rest. A register value r implies ~2^r distinct items; the harmonic mean of the registers estimates cardinality; small-range correction uses linear counting `m ln(m/V)`.
* **Welford**: `delta = x - mean; mean += delta/n; M2 += delta*(x - mean)`; variance = M2/(n-1). Avoids catastrophic cancellation of the naive `E[x^2]-E[x]^2`.
* **Reservoir (Algorithm R)**: keep first k; for item i>k replace a random slot with probability k/i.

### AI/analytics relevance
Feature stores (dedupe event ids with Bloom filters before expensive lookups), heavy-hitter detection for abuse/trending, unique-user counting across billions of events (BigQuery `APPROX_COUNT_DISTINCT`, Redis `PFCOUNT`, Druid), drift monitoring with streaming mean/variance, uniform down-sampling of training data from live traffic.

## 2. Memory Architecture Breakdown

```
Bloom (n=1M, p=1%)     m = 9.59M bits = 1.2 MB   vs  HashSet<Long> of 1M = ~ 50+ MB
Count-Min (d=5,w=2048) 10240 longs = 80 KB       vs  HashMap<Long,Long> of 1M keys ~ 70 MB
HyperLogLog (p=12)     4096 bytes (we use byte[]; production packs 6-bit => 3 KB)
```
* Bits live in a `long[]` -> contiguous, cache-friendly, trivially serialisable (`ByteBuffer`) and mergeable via bitwise OR (Bloom), element-wise add (CMS) or element-wise max (HLL).
* A Bloom filter's `k` probes hit `k` random cache lines: blocked Bloom filters (all k bits in one 64-byte line) trade a little accuracy for ~2-3x speed.
* Use `Long.remainderUnsigned`/`Math.floorMod` for bit indexes - a plain `%` on a negative hash yields a negative index.

