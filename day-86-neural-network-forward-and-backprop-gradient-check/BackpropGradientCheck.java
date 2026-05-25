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
 * Day 86 - Neural Network Backprop and Gradient Check (short mini-lab: a few lines of code + assertions).
 * Run: javac BackpropGradientCheck.java && java BackpropGradientCheck
 */
public class BackpropGradientCheck {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double x = 0.5, w = 0.3, y = 1;
DoubleUnaryOperator loss = ww -> { double p = 1 / (1 + Math.exp(-ww * x)); return -(y * Math.log(p) + (1 - y) * Math.log(1 - p)); };
double p = 1 / (1 + Math.exp(-w * x)), analytic = (p - y) * x, eps = 1e-6;
double numeric = (loss.applyAsDouble(w + eps) - loss.applyAsDouble(w - eps)) / (2 * eps);
check(Math.abs(analytic - numeric) < 1e-6, "analytic " + analytic + " vs numeric " + numeric);
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 86 OK");
    }
}
