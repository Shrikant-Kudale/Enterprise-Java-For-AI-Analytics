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
 * Day 54 - Prefix Sums and Difference Arrays (short mini-lab: a few lines of code + assertions).
 * Run: javac PrefixSumsDiffArrays.java && java PrefixSumsDiffArrays
 */
public class PrefixSumsDiffArrays {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int[] a = {3, 1, 4, 1, 5}; int[] p = new int[a.length + 1];
for (int i = 0; i < a.length; i++) p[i + 1] = p[i] + a[i];
check(p[4] - p[1] == 6, "sum of a[1..3]");
int[] d = new int[6]; d[1] += 2; d[4] -= 2; int run = 0; int[] out = new int[5];
for (int i = 0; i < 5; i++) { run += d[i]; out[i] = run; }
check(Arrays.equals(out, new int[]{0, 2, 2, 2, 0}), "range add via difference array");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 54 OK");
    }
}
