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
 * Day 74 - Distributed ID Generation (Snowflake) (short mini-lab: a few lines of code + assertions).
 * Run: javac SnowflakeIds.java && java SnowflakeIds
 */
public class SnowflakeIds {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static long last = -1, seq = 0;
    static synchronized long next(long ts, long node) { if (ts == last) seq++; else { seq = 0; last = ts; } return (ts << 22) | (node << 12) | seq; }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
long a = next(1000, 1), b = next(1000, 1), c = next(1001, 1);
check(a < b && b < c, "ids are time ordered and unique");
check(((a >> 12) & 0x3FF) == 1, "node bits embedded");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 74 OK");
    }
}
