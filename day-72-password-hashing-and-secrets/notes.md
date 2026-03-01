# Day 72 - Password Hashing and Secrets

> **Phase:** Backend Systems & Security | **Date:** 2026-05-11 | **Code:** [`Pbkdf2Passwords.java`](./Pbkdf2Passwords.java) | **Runtime:** JDK 21+

## 1. Core Concepts

Never store passwords or hash with plain SHA. Use a slow, salted KDF: **bcrypt/scrypt/Argon2** or PBKDF2 with high iterations and a random per-user salt. Keep secrets in a vault / env, not in git; rotate regularly.

## 2. Memory Architecture Breakdown

Cost factor trades CPU time for attacker cost.

## 3. Interview Edge Cases & Pitfalls

1. Unique salt per password.
2. Compare hashes in constant time.

## 4. Interview Q&A

**Q1. Why salt?**  
Defeats rainbow tables and identical-hash detection.

**Q2. Why slow hashes?**  
Make brute force expensive.

## 5. Run the Lab

```bash
cd day-72-password-hashing-and-secrets
javac Pbkdf2Passwords.java
java Pbkdf2Passwords
```
The program prints benchmark tables and finishes with `Day 72 OK` only if **every assertion passes** (assertions are plain `if/throw AssertionError` checks, so `-ea` is not required).

## 6. Self-Check Checklist

- [ ] I can explain every section above without looking at it.
- [ ] I changed one parameter in the benchmark, predicted the result, then measured it.
- [ ] I can answer each Q&A item in under 90 seconds, out loud.
- [ ] I broke one assertion on purpose and understood the failure message.
