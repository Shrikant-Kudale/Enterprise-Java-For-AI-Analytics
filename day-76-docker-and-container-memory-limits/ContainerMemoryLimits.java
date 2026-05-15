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
 * Day 76 - Docker and Container Memory Limits (short mini-lab: a few lines of code + assertions).
 * Run: javac ContainerMemoryLimits.java && java ContainerMemoryLimits
 */
public class ContainerMemoryLimits {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double limitMb = 2048, pct = 75, heap = limitMb * pct / 100;
check(heap == 1536.0, "MaxRAMPercentage=75 of 2 GB = 1536 MB heap");
check(Runtime.getRuntime().availableProcessors() >= 1, "JVM sees at least one CPU");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 76 OK");
    }
}
