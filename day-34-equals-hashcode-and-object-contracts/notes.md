# Day 34 - equals, hashCode and Object Contracts

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-03 | **Code:** [`EqualsHashCodeContract.java`](./EqualsHashCodeContract.java) | **Runtime:** JDK 21+

## 1. Core Concepts

`equals` must be reflexive, symmetric, transitive, consistent; equal objects need equal `hashCode`. Use immutable keys in hash collections. Records generate both.

## 2. Memory Architecture Breakdown

HashMap uses `hashCode` for the bucket then `equals` inside the bucket; a mutated key is lost in the wrong bucket.

## 3. Interview Edge Cases & Pitfalls

1. Overriding equals without hashCode breaks HashSet.
2. Don't use mutable fields in hashCode.

## 4. Interview Q&A

**Q1. What breaks if hashCode is inconsistent?**  
Lookups miss the entry.

**Q2. Why records for keys?**  
Auto value semantics.

## 5. Run the Lab

```bash
cd day-34-equals-hashcode-and-object-contracts
javac EqualsHashCodeContract.java
java EqualsHashCodeContract
```
The program prints benchmark tables and finishes with `Day 34 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
