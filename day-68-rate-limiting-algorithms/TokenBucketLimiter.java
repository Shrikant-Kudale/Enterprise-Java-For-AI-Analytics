import java.io.*;
import java.lang.management.*;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Day 68 - Rate Limiting Algorithms (short mini-lab: a few lines of code + assertions).
 * Run: javac TokenBucketLimiter.java && java TokenBucketLimiter
 */
public class TokenBucketLimiter {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static final class Bucket {
        double tokens; final double cap, perSec; long last;
        Bucket(double cap, double perSec) { this.cap = cap; this.perSec = perSec; tokens = cap; }
        boolean take(long nowMs) { tokens = Math.min(cap, tokens + (nowMs - last) * perSec / 1000.0); last = nowMs; if (tokens >= 1) { tokens -= 1; return true; } return false; }
    }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Bucket b = new Bucket(2, 1);
check(b.take(0) && b.take(0) && !b.take(0), "burst of 2 then rejected");
check(b.take(1000), "refilled 1 token after 1 s");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 68 OK");
    }
}
