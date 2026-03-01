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
 * Day 52 - Profiling, JFR and Thread Dumps (short mini-lab: a few lines of code + assertions).
 * Run: javac ThreadDumpsProfiling.java && java ThreadDumpsProfiling
 */
public class ThreadDumpsProfiling {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
ThreadInfo[] ti = ManagementFactory.getThreadMXBean().dumpAllThreads(false, false);
check(ti.length > 0 && Arrays.stream(ti).anyMatch(t -> t.getThreadName().equals("main")), "main thread appears in the dump");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 52 OK");
    }
}
