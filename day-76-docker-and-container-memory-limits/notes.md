# Day 76 - Docker and Container Memory Limits

> **Phase:** Backend Systems & Security | **Date:** 2026-05-15 | **Code:** [`ContainerMemoryLimits.java`](./ContainerMemoryLimits.java) | **Runtime:** JDK 21+

## 1. Core Concepts

JVMs are container-aware: `-XX:MaxRAMPercentage`, `-XX:ActiveProcessorCount`. Use multi-stage builds, a slim JRE image, non-root user, health checks, and `docker stats`/`jcmd VM.native_memory` to verify. Kubernetes: requests vs limits, liveness vs readiness.

## 2. Memory Architecture Breakdown

RSS = heap + metaspace + code cache + threads + direct + native libs.

## 3. Interview Edge Cases & Pitfalls

1. CPU limits cause throttling and GC pauses.
2. OOMKilled (exit 137) is not a Java OOM.

## 4. Interview Q&A

**Q1. Why OOMKilled with a small heap?**  
Native/direct/thread memory exceeded the limit.

**Q2. Requests vs limits?**  
Scheduling guarantee vs hard cap.

## 5. Run the Lab

```bash
cd day-76-docker-and-container-memory-limits
javac ContainerMemoryLimits.java
java ContainerMemoryLimits
```
The program prints benchmark tables and finishes with `Day 76 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
