# Day 58 - Tries and Autocomplete

> **Phase:** Algorithms & Data Structures Drills | **Date:** 2026-04-27 | **Code:** [`TrieAutocomplete.java`](./TrieAutocomplete.java) | **Runtime:** JDK 21+

## 1. Core Concepts

A **trie** stores strings by shared prefixes: insert/search O(L). Prefix queries power autocomplete, spell check and routing tables. Compress with radix trees; use sorted arrays + binary search for read-only sets.

## 2. Memory Architecture Breakdown

Each node holds a child map/array - memory-heavy for sparse alphabets.

## 3. Interview Edge Cases & Pitfalls

1. Case/unicode normalisation.
2. Limit suggestions (top-k by frequency).

## 4. Interview Q&A

**Q1. Trie vs HashMap?**  
Trie supports prefix queries; HashMap only exact lookup.

**Q2. Memory trade-off?**  
Fast prefix search for higher memory.

## 5. Run the Lab

```bash
cd day-58-tries-and-autocomplete
javac TrieAutocomplete.java
java TrieAutocomplete
```
The program prints benchmark tables and finishes with `Day 58 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
