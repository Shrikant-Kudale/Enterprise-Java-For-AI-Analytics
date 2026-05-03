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
 * Day 64 - Optimistic Locking and Versioning (short mini-lab: a few lines of code + assertions).
 * Run: javac OptimisticVersioning.java && java OptimisticVersioning
 */
public class OptimisticVersioning {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
AtomicInteger version = new AtomicInteger(1); int read = version.get(); version.incrementAndGet();
check(!version.compareAndSet(read, read + 1), "stale version rejected");
check(version.compareAndSet(2, 3), "fresh version accepted");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 64 OK");
    }
}
