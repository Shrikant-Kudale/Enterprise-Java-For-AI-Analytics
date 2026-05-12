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
 * Day 73 - Metrics, Histograms and Percentiles (short mini-lab: a few lines of code + assertions).
 * Run: javac LatencyPercentiles.java && java LatencyPercentiles
 */
public class LatencyPercentiles {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double[] lat = IntStream.rangeClosed(1, 100).asDoubleStream().toArray();
double p99 = lat[(int) Math.ceil(0.99 * lat.length) - 1];
check(p99 == 99.0 && Arrays.stream(lat).average().orElse(0) == 50.5, "p99 = 99 while the mean (50.5) hides the tail");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 73 OK");
    }
}
