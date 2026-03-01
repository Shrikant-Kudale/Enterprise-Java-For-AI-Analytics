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
 * Day 78 - Logistic Regression from Scratch (short mini-lab: a few lines of code + assertions).
 * Run: javac LogisticRegressionGD.java && java LogisticRegressionGD
 */
public class LogisticRegressionGD {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double[] x = {0, 1, 2, 3}; int[] y = {0, 0, 1, 1}; double w = 0, b = 0;
for (int ep = 0; ep < 5000; ep++) { double gw = 0, gb = 0; for (int i = 0; i < 4; i++) { double p = 1 / (1 + Math.exp(-(w * x[i] + b))); gw += (p - y[i]) * x[i]; gb += p - y[i]; } w -= 0.1 * gw; b -= 0.1 * gb; }
check(1 / (1 + Math.exp(-(w * 3 + b))) > 0.5 && 1 / (1 + Math.exp(-b)) < 0.5, "separates the classes");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 78 OK");
    }
}
