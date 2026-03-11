# Day 11 - Comparators, Sorting and Binary Search

> **Phase:** Collections & Data Structures | **Date:** 2026-03-11 | **Code:** [`SortingAndSearch.java`](./SortingAndSearch.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### Comparator contract
`compare(a,b)` must be **antisymmetric** (`sgn(c(a,b)) == -sgn(c(b,a))`), **transitive**, and ideally **consistent with equals** (needed for `TreeSet/TreeMap`, otherwise "equal" keys vanish). Violations make TimSort throw `IllegalArgumentException: Comparison method violates its general contract!`.

Build comparators declaratively:
```java
Comparator.comparingDouble(Prediction::score).reversed()
          .thenComparingLong(Prediction::ts)
          .thenComparing(Prediction::id, Comparator.nullsLast(Comparator.naturalOrder()))
```
Never subtract (`a - b` overflows); use `Integer.compare`/`Double.compare`. `Double.compare` orders `-0.0 < 0.0` and puts `NaN` last, unlike `<`.

### JDK sorting algorithms
| API | Algorithm | Stable | Worst case |
|-----|-----------|--------|-----------|
| `Arrays.sort(int[]/double[]...)` | **Dual-pivot quicksort** (+ insertion sort for small, merge for runs) | n/a (primitives) | O(n log n) in practice, JDK 14+ has heap-sort fallback |
| `Arrays.sort(Object[])`, `List.sort`, `Collections.sort` | **TimSort** (natural runs + galloping merge) | **yes** | O(n log n), O(n) on presorted data |
| `Arrays.parallelSort` | fork-join merge of sorted chunks | yes (objects) | speed-up on multi-core for >8192 elements |

TimSort exploits real-world partially ordered data: detects ascending/descending *runs*, extends short ones to `minrun` (32-64) via binary insertion sort, then merges runs maintaining a stack invariant. Needs up to n/2 temp space.

### Sorting algorithm menu for interviews
Merge sort (stable, O(n) extra), heap sort (in place, not stable), quicksort (3-way partition for many duplicates, randomised pivot, recurse into smaller side to bound stack at O(log n)), counting/radix sort (O(n+k) for bounded integer keys), quickselect (k-th element O(n) average).

### Binary search
Invariant-based thinking prevents off-by-one bugs: maintain `lo` as "all indices < lo are too small" and `hi` as "all indices >= hi are too big".
* **lowerBound**: first index with `a[i] >= x`; **upperBound**: first index with `a[i] > x`; count of x = upper - lower.
* `Arrays.binarySearch` returns `-(insertionPoint) - 1` when missing (and *any* matching index among duplicates).
* Midpoint: `(lo + hi) >>> 1` - `(lo+hi)/2` overflowed in the JDK itself until 2006.
* **Binary search on the answer**: any monotone predicate (min batch size meeting throughput, min replicas meeting SLA, max model size fitting memory) can be bisected - a favourite FAANG pattern.

### AI backend relevance
Ranking model outputs, merging top-k from shards, picking a threshold from a sorted score list (ROC curves), bucketising features with sorted boundaries (`binarySearch` into quantile cut-points), timeline lookups in time-series feature stores.

