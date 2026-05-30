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
 * Day 91 - Mock System Design: Capacity Math (short mini-lab: a few lines of code + assertions).
 * Run: javac SystemDesignMath.java && java SystemDesignMath
 */
public class SystemDesignMath {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double qps = 5000, p99 = 0.04, perReplica = 8, util = 0.6;
int replicas = (int) Math.ceil(qps * p99 / perReplica / util);
check(replicas == 42, "replicas = " + replicas);
double terabytes = 1_000_000_000L * 768 * 4 / 1e12;
check(Math.abs(terabytes - 3.072) < 1e-9, "1B x 768 float32 embeddings = 3.07 TB");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 91 OK");
    }
}
