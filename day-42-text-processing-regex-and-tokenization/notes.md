# Day 42 - Regex and Tokenization

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-11 | **Code:** [`RegexTokenization.java`](./RegexTokenization.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Tokenise with `split`/`Pattern`, lower-case, strip punctuation. Precompile `Pattern`s (static final); use named groups. Beware catastrophic backtracking (`(a+)+`).

## 2. Memory Architecture Breakdown

`Pattern` is immutable/thread-safe; `Matcher` is not.

## 3. Interview Edge Cases & Pitfalls

1. Regex in hot loops without precompile.
2. Unicode: use `\\p{L}` not `[a-z]`.

## 4. Interview Q&A

**Q1. Why precompile patterns?**  
Compilation is expensive; reuse the Pattern.

**Q2. What is ReDoS?**  
Exponential backtracking on crafted input.

## 5. Run the Lab

```bash
cd day-42-text-processing-regex-and-tokenization
javac RegexTokenization.java
java RegexTokenization
```
The program prints benchmark tables and finishes with `Day 42 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
