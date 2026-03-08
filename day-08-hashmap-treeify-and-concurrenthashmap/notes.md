# Day 08 - HashMap, Treeification and ConcurrentHashMap

> **Phase:** Collections & Data Structures | **Date:** 2026-03-08 | **Code:** [`HashMapInternals.java`](./HashMapInternals.java) | **Runtime:** JDK 21+

## 1. Core Concepts

### HashMap algorithm (Java 8+)
1. `h = key.hashCode(); h ^= h >>> 16` - **spread** high bits into low bits because the index uses only the low bits.
2. `index = (n - 1) & h` - table length `n` is always a power of two so `&` replaces `%`.
3. Bucket holds a singly-linked list; new nodes are appended at the **tail**.
4. If a chain reaches **TREEIFY_THRESHOLD = 8** *and* table length >= **MIN_TREEIFY_CAPACITY = 64**, the bin becomes a **red-black tree** (`TreeNode`), ordered by hash then `Comparable` (if the key's class implements `Comparable<itself>`) then identity tie-break. If the table is smaller than 64 it **resizes instead**. Trees shrink back to lists at **UNTREEIFY_THRESHOLD = 6**.
5. `size > threshold (capacity * 0.75)` -> **resize** to 2x; each bin splits into a "lo" and "hi" list preserving order (no re-hash needed: the new bit decides).
6. Defaults: capacity 16, load factor 0.75, lazily allocated. `new HashMap<>(expected / 0.75 + 1)` avoids resizes (`HashMap.newHashMap(n)` in Java 19+).

Complexity: average O(1); worst-case O(log n) per lookup thanks to treeification (was O(n) before Java 8) - **only** if keys are `Comparable`; for non-comparable keys with equal hashes lookups in a tree bin may still scan O(n).

### Hash-flooding (algorithmic complexity attack)
An attacker submitting thousands of keys with identical hashes (JSON field names, query params) degrades a service to O(n^2). Java 8 treeification is the mitigation; also cap request sizes and prefer `Comparable` keys.

### ConcurrentHashMap (Java 8+)
* **No segments** any more: CAS to insert into an empty bin, `synchronized` on the *first node of the bin* for collisions - lock granularity = one bucket.
* Reads are lock-free (`volatile` table/next/val fields).
* Atomic compound ops: `putIfAbsent`, `compute`, `computeIfAbsent`, `merge` (the mapping function runs under the bin lock - keep it short, never touch the same map inside it).
* Size is a striped counter (`CounterCell[]`, like `LongAdder`): `size()` is an *estimate* under concurrent updates; use `mappingCount()` (long).
* No null keys or values (null would be ambiguous with "absent" in concurrent `get`).
* Resizing is cooperative: threads that find a forwarding node help transfer bins.
* Iterators are **weakly consistent** (never throw CME, may or may not reflect concurrent updates).

### Siblings
`LinkedHashMap` (insertion/access order -> LRU via `removeEldestEntry`), `TreeMap` (red-black, sorted, O(log n)), `IdentityHashMap`, `EnumMap`, `WeakHashMap`, `ConcurrentSkipListMap` (sorted + concurrent).

## 2. Memory Architecture Breakdown

```
table (n=16)      Node: [hash 4B][key ref][val ref][next ref] = 32B (12 hdr + 4 + 4*3 -> 32)
 [0] -> null
 [1] -> N --> N --> N            chain <8   : linked list
 [5] -> TreeNode(red/black)      chain>=8 & n>=64 : TreeNode = 56B+ (parent,left,right,prev,red)
 ...
Resize 16->32: bin j splits into j (lo) and j+16 (hi) depending on (hash & oldCap)

Per-entry cost (HashMap<Integer,Integer>): 32B node + 16B key + 16B value + 4B table slot ~ 70B   (vs 8B in int[]/int[])
```
* HashMap is **not thread-safe**: concurrent resize in Java 7 could create a cyclic bucket list (100% CPU infinite loop); Java 8 can instead lose updates or return stale/null values. Never share without synchronisation.
* `ConcurrentHashMap.CounterCell[]` adds padding (`@Contended`) to avoid false sharing - one reason it's larger than HashMap.
* `HashSet` is a `HashMap` with a dummy `PRESENT` value - 1 extra ref per entry versus a purpose-built open-addressing set.

## 3. Interview Edge Cases & Pitfalls

1. Mutating a key's fields after insertion changes `hashCode` -> entry becomes unreachable (leak). Keys must be immutable.
2. Override `equals` **and** `hashCode` together; equal objects must have equal hashes, the converse isn't required.
3. `HashMap` permits one `null` key and null values; `ConcurrentHashMap`/`Hashtable`/`Map.of` don't.
4. `computeIfAbsent` with a recursive mapping function that modifies the same map -> `IllegalStateException: Recursive update` (CHM) or CME (HashMap, Java 9+).
5. Treeify requires table >= 64: with a small table 8 collisions just trigger a resize.
6. Iteration order of HashMap is unspecified and changes with capacity - never depend on it (use `LinkedHashMap`/`TreeMap`).
7. `ConcurrentHashMap.size()` is only an estimate under contention; check-then-act (`if(!m.containsKey) m.put`) is a race - use `putIfAbsent`.
8. `Collections.synchronizedMap` locks the entire map on each call, and iteration still needs manual `synchronized(map)`.

## 4. Interview Q&A

**Q1. Explain how HashMap works internally.**  
Array of buckets indexed by `(n-1) & spread(hash)`, chaining on collision, tail insertion, treeify at chain >= 8 with n >= 64, resize doubling at load factor 0.75 with lo/hi split.

**Q2. Why is the table size a power of two?**  
So index = `hash & (n-1)` (a single AND) instead of modulo, and so resize can split bins using just one extra hash bit.

**Q3. Why `h ^ (h >>> 16)`?**  
With small tables only the low bits select the bucket; XOR-ing the upper 16 bits into the lower half lets high-bit differences influence the index and reduces collisions for hashes that differ only in high bits (e.g. floats, doubles).

**Q4. How does ConcurrentHashMap achieve concurrency without a global lock?**  
CAS for empty-bin insertion, per-bin `synchronized` on the head node otherwise, volatile reads for lock-free `get`, striped size counters, cooperative resizing.

**Q5. When does HashMap degrade to O(n) and how does Java mitigate it?**  
When many keys share a bucket (poor hashCode / flooding). Java 8 treeifies bins to O(log n) provided keys are Comparable or hash-distinct enough.

**Q6. How would you build a thread-safe word-frequency counter?**  
`ConcurrentHashMap<String, LongAdder>` using `computeIfAbsent(k, x -> new LongAdder()).increment()` - or `merge(k, 1L, Long::sum)` for low contention.

**Q7. HashMap vs TreeMap vs LinkedHashMap?**  
Hash: O(1) unordered. Tree: O(log n) sorted/navigable (floor, ceiling, range views). Linked: O(1) with predictable insertion/access order; `accessOrder=true` + `removeEldestEntry` = LRU cache.

## 5. Run the Lab

```bash
cd day-08-hashmap-treeify-and-concurrenthashmap
javac HashMapInternals.java
java HashMapInternals
```
The program prints benchmark tables and finishes with `Day 08 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
