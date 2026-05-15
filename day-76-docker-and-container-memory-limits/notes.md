# Day 76 - Docker and Container Memory Limits

> **Phase:** Backend Systems & Security | **Date:** 2026-05-15 | **Code:** [`ContainerMemoryLimits.java`](./ContainerMemoryLimits.java) | **Runtime:** JDK 21+

## 1. Core Concepts

JVMs are container-aware: `-XX:MaxRAMPercentage`, `-XX:ActiveProcessorCount`. Use multi-stage builds, a slim JRE image, non-root user, health checks, and `docker stats`/`jcmd VM.native_memory` to verify. Kubernetes: requests vs limits, liveness vs readiness.

