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
 * Day 70 - Bulkhead and Backpressure (short mini-lab: a few lines of code + assertions).
 * Run: javac BulkheadBackpressure.java && java BulkheadBackpressure
 */
public class BulkheadBackpressure {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Semaphore bulk = new Semaphore(2);
check(bulk.tryAcquire() && bulk.tryAcquire() && !bulk.tryAcquire(), "third concurrent call rejected");
ArrayBlockingQueue<Integer> q = new ArrayBlockingQueue<>(2);
check(q.offer(1) && q.offer(2) && !q.offer(3), "bounded queue pushes back");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 70 OK");
    }
}
