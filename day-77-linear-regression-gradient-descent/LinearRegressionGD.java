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
 * Day 77 - Linear Regression with Gradient Descent (short mini-lab: a few lines of code + assertions).
 * Run: javac LinearRegressionGD.java && java LinearRegressionGD
 */
public class LinearRegressionGD {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double w = 0, b = 0; double[] x = {1, 2, 3, 4}, y = {3, 5, 7, 9};
for (int ep = 0; ep < 10_000; ep++) { double gw = 0, gb = 0; for (int i = 0; i < 4; i++) { double err = w * x[i] + b - y[i]; gw += err * x[i]; gb += err; } w -= 0.05 * gw / 4; b -= 0.05 * gb / 4; }
check(Math.abs(w - 2) < 1e-2 && Math.abs(b - 1) < 1e-2, "learned y = 2x + 1 (w=" + w + ", b=" + b + ")");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 77 OK");
    }
}
