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
 * Day 63 - Transactions and Isolation Levels (short mini-lab: a few lines of code + assertions).
 * Run: javac TransactionIsolationToy.java && java TransactionIsolationToy
 */
public class TransactionIsolationToy {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Map<String, Integer> db = new HashMap<>(Map.of("a", 100));
Map<String, Integer> snapshot = new HashMap<>(db); snapshot.put("a", 50);
check(db.get("a") == 100, "uncommitted writes are isolated from other readers");
db.putAll(snapshot); check(db.get("a") == 50, "commit publishes the change");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 63 OK");
    }
}
