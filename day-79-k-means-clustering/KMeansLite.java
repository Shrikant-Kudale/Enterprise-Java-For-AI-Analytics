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
 * Day 79 - K-Means Clustering (short mini-lab: a few lines of code + assertions).
 * Run: javac KMeansLite.java && java KMeansLite
 */
public class KMeansLite {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double[] pts = {1, 1.2, 0.8, 10, 10.2, 9.8}; double c0 = 0, c1 = 5;
for (int it = 0; it < 10; it++) { double s0 = 0, s1 = 0; int n0 = 0, n1 = 0; for (double p : pts) { if (Math.abs(p - c0) <= Math.abs(p - c1)) { s0 += p; n0++; } else { s1 += p; n1++; } } if (n0 > 0) c0 = s0 / n0; if (n1 > 0) c1 = s1 / n1; }
check(Math.abs(c0 - 1) < 1e-9 && Math.abs(c1 - 10) < 1e-9, "centroids converge to 1 and 10");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 79 OK");
    }
}
