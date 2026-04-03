# Day 34 - equals, hashCode and Object Contracts

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-03 | **Code:** [`EqualsHashCodeContract.java`](./EqualsHashCodeContract.java) | **Runtime:** JDK 21+

## 1. Core Concepts

`equals` must be reflexive, symmetric, transitive, consistent; equal objects need equal `hashCode`. Use immutable keys in hash collections. Records generate both.

