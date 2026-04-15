# Day 46 - Unit Testing and Property Tests

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-15 | **Code:** [`MiniPropertyTests.java`](./MiniPropertyTests.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Good tests are fast, isolated and deterministic: arrange-act-assert, one behaviour per test, fakes over mocks. **Property tests** assert invariants over many random inputs (jqwik). JUnit 5 + AssertJ + Mockito/Testcontainers in practice.

