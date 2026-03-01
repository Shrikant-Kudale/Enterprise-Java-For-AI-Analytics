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
 * Day 87 - Model Serving: Batching and Latency Budgets (short mini-lab: a few lines of code + assertions).
 * Run: javac ServingBatchBudget.java && java ServingBatchBudget
 */
public class ServingBatchBudget {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int fixedUs = 2000, perItemUs = 100, budgetUs = 10_000;
int maxBatch = (budgetUs - fixedUs) / perItemUs;
check(maxBatch == 80, "largest batch inside a 10 ms budget");
check((double) maxBatch / (fixedUs + perItemUs * maxBatch) > 1.0 / (fixedUs + perItemUs), "batching raises throughput");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 87 OK");
    }
}
