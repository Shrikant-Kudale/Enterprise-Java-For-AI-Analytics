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
 * Day 84 - Embeddings, Cosine Similarity and kNN (short mini-lab: a few lines of code + assertions).
 * Run: javac CosineNearestNeighbor.java && java CosineNearestNeighbor
 */
public class CosineNearestNeighbor {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static double cos(double[] a, double[] b) { double d = 0, na = 0, nb = 0; for (int i = 0; i < a.length; i++) { d += a[i] * b[i]; na += a[i] * a[i]; nb += b[i] * b[i]; } return d / Math.sqrt(na * nb); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double[][] db = {{1, 0}, {0.9, 0.1}, {0, 1}}; double[] q = {1, 0.05}; int best = 0;
for (int i = 1; i < db.length; i++) if (cos(q, db[i]) > cos(q, db[best])) best = i;
check(best == 0 && Math.abs(cos(q, q) - 1) < 1e-12, "nearest neighbour and self-similarity");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 84 OK");
    }
}
