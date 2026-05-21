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
 * Day 82 - Evaluation Metrics: Precision, Recall, AUC (short mini-lab: a few lines of code + assertions).
 * Run: javac ClassificationMetrics.java && java ClassificationMetrics
 */
public class ClassificationMetrics {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int[] y = {1, 1, 0, 1, 0}, p = {1, 0, 0, 1, 1}; int tp = 0, fp = 0, fn = 0;
for (int i = 0; i < y.length; i++) { if (p[i] == 1 && y[i] == 1) tp++; if (p[i] == 1 && y[i] == 0) fp++; if (p[i] == 0 && y[i] == 1) fn++; }
check(Math.abs(tp / (double) (tp + fp) - 2.0 / 3) < 1e-12 && Math.abs(tp / (double) (tp + fn) - 2.0 / 3) < 1e-12, "precision = recall = 2/3");
double[] s = {0.9, 0.8, 0.3, 0.7, 0.2}; int pairs = 0, wins = 0;
for (int i = 0; i < y.length; i++) for (int j = 0; j < y.length; j++) if (y[i] == 1 && y[j] == 0) { pairs++; if (s[i] > s[j]) wins++; }
check(wins == pairs, "AUC = 1.0 for perfectly ranked scores");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 82 OK");
    }
}
