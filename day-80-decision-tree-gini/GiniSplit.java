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
 * Day 80 - Decision Trees and Gini Impurity (short mini-lab: a few lines of code + assertions).
 * Run: javac GiniSplit.java && java GiniSplit
 */
public class GiniSplit {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static double gini(int[] y) { double p = Arrays.stream(y).average().orElse(0); return 2 * p * (1 - p); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
check(gini(new int[]{0, 0, 1, 1}) == 0.5 && gini(new int[]{1, 1, 1}) == 0.0, "impurity extremes");
double[] x = {1, 2, 3, 4}; int[] y = {0, 0, 1, 1}; double bestT = -1, best = 9;
for (double t : new double[]{1.5, 2.5, 3.5}) {
    int[] l = IntStream.range(0, 4).filter(i -> x[i] <= t).map(i -> y[i]).toArray(), r = IntStream.range(0, 4).filter(i -> x[i] > t).map(i -> y[i]).toArray();
    double g = (l.length * gini(l) + r.length * gini(r)) / 4; if (g < best) { best = g; bestT = t; }
}
check(bestT == 2.5, "best split threshold");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 80 OK");
    }
}
