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
 * Day 47 - JMH-style Benchmarking Pitfalls (short mini-lab: a few lines of code + assertions).
 * Run: javac BenchmarkPitfalls.java && java BenchmarkPitfalls
 */
public class BenchmarkPitfalls {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
long sum = 0; for (int i = 0; i < 50_000_000; i++) sum += i;
check(sum == 1_249_999_975_000_000L, "closed form n(n-1)/2");
System.out.println("result must be consumed so the loop is not eliminated: " + sum);
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 47 OK");
    }
}
