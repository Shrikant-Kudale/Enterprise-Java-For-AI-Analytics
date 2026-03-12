# Day 12 - Big-O Optimization in Java Collections

> **Phase:** Collections & Data Structures | **Date:** 2026-03-12 | **Code:** [`BigOCollections.java`](./BigOCollections.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Complexity cheat-sheet for the JDK
| Structure | get / contains | add | remove | Notes |
|-----------|---------------|-----|--------|-------|
| `ArrayList` | O(1) / O(n) | O(1)* | O(n) | *amortised; `remove(0)` is O(n) |
| `LinkedList` | O(n) / O(n) | O(1) at ends | O(1) at known node | poor locality |
| `ArrayDeque` | - | O(1) both ends | O(1) both ends | best queue/stack |
| `HashMap/HashSet` | O(1) avg (O(log n) worst) | O(1)* | O(1) | needs good hashCode |
| `LinkedHashMap` | O(1) | O(1) | O(1) | ordered |
| `TreeMap/TreeSet` | O(log n) | O(log n) | O(log n) | sorted, range queries |
| `PriorityQueue` | peek O(1) | O(log n) | poll O(log n), remove(o) O(n) | |
| `ConcurrentHashMap` | O(1) | O(1) | O(1) | |
| `String +=` in loop | - | O(n) each -> O(n^2) | - | use `StringBuilder` |

### Optimisation playbook (in priority order)
1. **Change the algorithm / data structure** (O(n^2) -> O(n log n) -> O(n)). Replace nested loops with a `HashMap`/`HashSet` index, sort + two pointers, prefix sums, heaps, sliding windows.
2. **Avoid hidden O(n) calls inside loops**: `list.contains`, `list.remove(0)`, `list.indexOf`, `String.substring` loops, `LinkedList.get(i)`, `stream().filter(...).findFirst()` per iteration, `String.format`.
3. **Presize** collections (`new ArrayList<>(n)`, `HashMap.newHashMap(n)` / `n/0.75+1`) - avoids rehash and copy.
4. **Reduce constant factors**: primitives vs boxed, arrays vs collections, `StringBuilder`, avoid regex in hot loops, reuse buffers, avoid allocation.
5. **Exploit hardware**: cache locality (struct-of-arrays), batch work, parallelism (only after 1-4).
6. **Measure**: profile first (JFR, async-profiler); use JMH for microbenchmarks. Our simple harness shows trends; JMH controls JIT/GC artefacts.

### Amortised vs worst-case
Dynamic array append is amortised O(1) but a single call may copy n elements - bad for latency-critical paths (p99!). Rehash in HashMap has the same pattern. Presize or use structures with predictable worst-case.

### Empirical complexity
Double n and measure: ratio ~2 -> O(n), ~4 -> O(n^2), ~2.1 -> O(n log n). `exponent = log2(t(2n)/t(n))`. Warm up first and take the minimum of several runs.

### Interview communication template
State the brute force and its complexity -> identify the bottleneck operation -> name the data structure that makes it cheaper -> state the new time *and space* -> mention trade-offs (memory vs speed, preprocessing cost).

## 2. Memory Architecture Breakdown

```
Space-time trade-off in "two sum":
  brute force : O(1) extra space, O(n^2) time   (n=20k -> 200M comparisons)
  hash index  : O(n) extra space, O(n) time     (HashMap<Integer,Integer>: ~70 B/entry -> 1.4 MB)

String s += "x" (n times):   each concat allocates a NEW String + byte[] of length i -> garbage = n^2/2 bytes
StringBuilder:               one growing byte[] (doubling) -> O(n) bytes copied in total
```
* `HashMap` default growth copies ~2n entries total: presizing for 1M entries saves ~19 resizes (and ~100 MB of short-lived garbage).
* `ArrayList.remove(0)` shifts n references: for 100k elements that is ~400 KB of memmove per call - 40 GB of memory traffic for a full drain.
* Complexity ignores constants and cache: `O(n)` over a `LinkedList` can lose to `O(n log n)` over an array for realistic n.

## 3. Interview Edge Cases & Pitfalls

1. `list.contains()` inside a loop silently turns O(n) into O(n^2) - the single most common interview and production perf bug.
2. `String.intern`, `substring` (copy since Java 7u6), `+=` in loops, `String.split` + regex compile cost.
3. `stream().distinct().count()` allocates a LinkedHashSet-like structure; `parallel` doesn't make poor algorithms good.
4. Big-O hides constants: for n < ~50 a linear scan beats a HashMap lookup; JDK `Arrays.sort` uses insertion sort for tiny ranges.
5. `HashMap` is O(1) only with a decent `hashCode`; mutable keys and constant hashes destroy it (day 8).
6. `TreeMap.size()` is O(1) but `subMap(...).size()` is O(k) (counts by iterating).
7. `Collections.sort(linkedList)` copies to an array first - O(n log n) is still fine, but `LinkedList.get` loops are O(n^2).
8. Amortised analysis: `ArrayList.add` is fine for throughput but a rehash/copy pause can blow a p99 SLO.

