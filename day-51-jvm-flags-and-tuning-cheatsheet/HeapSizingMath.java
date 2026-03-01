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
 * Day 51 - JVM Flags and Heap Sizing (short mini-lab: a few lines of code + assertions).
 * Run: javac HeapSizingMath.java && java HeapSizingMath
 */
public class HeapSizingMath {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double limitMb = 4096, heap = limitMb * 0.70;
double budget = heap + 256 /*metaspace*/ + 512 /*direct*/ + 200 /*stacks*/;
check(budget < limitMb, "total JVM budget fits the container: " + budget);
check(Runtime.getRuntime().maxMemory() > 0, "maxMemory visible");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 51 OK");
    }
}
