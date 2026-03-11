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

## 2. Memory Architecture Breakdown

```
Object[] sort                          int[] sort
 [ref][ref][ref]...  -> scattered      [4B][4B][4B]...  contiguous
 each compare = 2 pointer derefs       each compare = register compare
 TimSort temp: n/2 refs                dual-pivot: in place (O(log n) stack)

List.sort -> toArray -> Arrays.sort(T[], c) -> write back via ListIterator/arraycopy
```
* Sorting `List<Integer>` of 1M elements touches 1M boxed objects (cache misses) - typically **3-6x slower** than `int[]`.
* Sorting an array of records by a field is cache-hostile; for very hot paths sort an `int[]` of indices by a `double[]` of keys, or pack `(key, index)` into a `long` and sort primitives.
* TimSort's merge scratch space is allocated per call (up to n/2) - memory spike for big lists; `parallelSort` allocates a full n-sized working buffer.

## 3. Interview Edge Cases & Pitfalls

1. `list.sort(null)` uses natural ordering and throws `ClassCastException` for non-Comparable elements.
2. Comparator using `(a,b) -> (int)(a.score - b.score)` truncates fractions (0.9 vs 0.1 -> 0) and violates transitivity.
3. `TreeSet` with a comparator that ignores a field treats objects as duplicates and silently drops them.
4. `Arrays.sort(Integer[], Collections.reverseOrder())` works; `Arrays.sort(int[], reverseOrder)` doesn't compile - sort and reverse, or box.
5. Sorting stability: sorting by score then by country as two passes only works if the **second** sort is stable and uses the *primary* key last.
6. Binary search on unsorted or on a `LinkedList` (`Collections.binarySearch` degrades to O(n) node walks).
7. Binary search for a floating-point answer: loop a fixed number of iterations (e.g. 100) instead of `lo != hi`; epsilon comparisons can livelock.
8. Quicksort with a fixed pivot (first element) is O(n^2) on sorted input - randomise / median-of-three.

