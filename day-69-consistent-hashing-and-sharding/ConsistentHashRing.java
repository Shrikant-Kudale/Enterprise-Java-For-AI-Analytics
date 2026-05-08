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
 * Day 69 - Consistent Hashing and Sharding (short mini-lab: a few lines of code + assertions).
 * Run: javac ConsistentHashRing.java && java ConsistentHashRing
 */
public class ConsistentHashRing {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static int h(String s) { int x = s.hashCode(); x ^= x >>> 16; x *= 0x85ebca6b; x ^= x >>> 13; return x & 0x7fffffff; }
    static String owner(TreeMap<Integer, String> r, String k) { var e = r.ceilingEntry(h(k)); return (e != null ? e : r.firstEntry()).getValue(); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
TreeMap<Integer, String> ring = new TreeMap<>();
for (String n : List.of("A", "B", "C")) for (int v = 0; v < 100; v++) ring.put(h(n + "#" + v), n);
Map<String, String> before = new HashMap<>(); for (int i = 0; i < 1000; i++) before.put("k" + i, owner(ring, "k" + i));
ring.values().removeIf(n -> n.equals("C"));
for (var e : before.entrySet()) if (!e.getValue().equals("C")) check(owner(ring, e.getKey()).equals(e.getValue()), "keys not on C keep their owner");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 69 OK");
    }
}
