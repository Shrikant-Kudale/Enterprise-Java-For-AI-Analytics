# Day 42 - Regex and Tokenization

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-11 | **Code:** [`RegexTokenization.java`](./RegexTokenization.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Tokenise with `split`/`Pattern`, lower-case, strip punctuation. Precompile `Pattern`s (static final); use named groups. Beware catastrophic backtracking (`(a+)+`).

## 2. Memory Architecture Breakdown

`Pattern` is immutable/thread-safe; `Matcher` is not.

