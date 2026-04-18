# Day 49 - Class Loading Basics

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-18 | **Code:** [`ClassLoadingBasics.java`](./ClassLoadingBasics.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Class loaders form a delegation hierarchy: bootstrap -> platform -> application. Parent-first delegation prevents core class spoofing. `ServiceLoader` discovers plugins via `META-INF/services` or `module-info`.

