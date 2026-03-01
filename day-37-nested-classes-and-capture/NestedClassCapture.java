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
 * Day 37 - Nested Classes and Variable Capture (short mini-lab: a few lines of code + assertions).
 * Run: javac NestedClassCapture.java && java NestedClassCapture
 */
public class NestedClassCapture {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static class Counter { int c; Runnable inc() { return () -> c++; } }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int base = 10; Function<Integer, Integer> add = x -> x + base;
check(add.apply(5) == 15, "captured local");
Counter k = new Counter(); Runnable r = k.inc(); r.run(); r.run();
check(k.c == 2, "lambda mutates a field of the captured this");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 37 OK");
    }
}
