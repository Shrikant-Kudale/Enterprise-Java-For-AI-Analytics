# Day 49 - Class Loading Basics

> **Phase:** Java Core Extras & JVM Tooling | **Date:** 2026-04-18 | **Code:** [`ClassLoadingBasics.java`](./ClassLoadingBasics.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Class loaders form a delegation hierarchy: bootstrap -> platform -> application. Parent-first delegation prevents core class spoofing. `ServiceLoader` discovers plugins via `META-INF/services` or `module-info`.

## 2. Memory Architecture Breakdown

Loaded classes live in Metaspace and are unloaded only when their loader is garbage collected.

## 3. Interview Edge Cases & Pitfalls

1. Custom loaders can leak Metaspace (redeploys).
2. Same class name from two loaders = different types.

## 4. Interview Q&A

**Q1. Parent-first delegation?**  
A loader asks its parent first, so core classes cannot be replaced.

**Q2. Why Metaspace leaks on redeploy?**  
Old loaders stay reachable via statics/threads.

