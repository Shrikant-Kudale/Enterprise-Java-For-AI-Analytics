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
 * Day 65 - Idempotency and Exactly-Once Semantics (short mini-lab: a few lines of code + assertions).
 * Run: javac IdempotencyKeys.java && java IdempotencyKeys
 */
public class IdempotencyKeys {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
ConcurrentHashMap<String, String> seen = new ConcurrentHashMap<>(); AtomicInteger charges = new AtomicInteger();
for (int i = 0; i < 3; i++) seen.computeIfAbsent("key-1", k -> { charges.incrementAndGet(); return "receipt"; });
check(charges.get() == 1 && seen.get("key-1").equals("receipt"), "charged exactly once");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 65 OK");
    }
}
