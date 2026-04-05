# Day 36 - Interfaces, Default Methods and Mixins

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-05 | **Code:** [`DefaultMethodsMixins.java`](./DefaultMethodsMixins.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Default methods let interfaces evolve and act as mixins (behaviour without state). Conflicts must be resolved explicitly with `Iface.super.method()`.

