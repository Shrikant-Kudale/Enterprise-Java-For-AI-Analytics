# Day 46 - Unit Testing and Property Tests

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-15 | **Code:** [`MiniPropertyTests.java`](./MiniPropertyTests.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Good tests are fast, isolated and deterministic: arrange-act-assert, one behaviour per test, fakes over mocks. **Property tests** assert invariants over many random inputs (jqwik). JUnit 5 + AssertJ + Mockito/Testcontainers in practice.

## 2. Memory Architecture Breakdown

Seed random generators so failures reproduce.

## 3. Interview Edge Cases & Pitfalls

1. Time and randomness make tests flaky - inject clocks/seeds.
2. Over-mocking tests implementation, not behaviour.

## 4. Interview Q&A

**Q1. Unit vs integration test?**  
Unit isolates logic; integration verifies wiring with real dependencies.

**Q2. What is a property test?**  
A rule that must hold for all generated inputs.

