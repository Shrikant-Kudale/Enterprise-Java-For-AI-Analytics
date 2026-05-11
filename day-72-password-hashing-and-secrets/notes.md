# Day 72 - Password Hashing and Secrets

> **Phase:** Backend Systems & Security | **Date:** 2026-05-11 | **Code:** [`Pbkdf2Passwords.java`](./Pbkdf2Passwords.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Never store passwords or hash with plain SHA. Use a slow, salted KDF: **bcrypt/scrypt/Argon2** or PBKDF2 with high iterations and a random per-user salt. Keep secrets in a vault / env, not in git; rotate regularly.

## 2. Memory Architecture Breakdown

Cost factor trades CPU time for attacker cost.

