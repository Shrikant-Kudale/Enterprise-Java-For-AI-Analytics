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
 * Day 90 - Mock Coding Round: Drills (short mini-lab: a few lines of code + assertions).
 * Run: javac CodingRoundDrills.java && java CodingRoundDrills
 */
public class CodingRoundDrills {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Map<Integer, Integer> seen = new HashMap<>(); int[] a = {2, 7, 11, 15}; int[] ans = null;
for (int i = 0; i < a.length; i++) { Integer j = seen.get(9 - a[i]); if (j != null) { ans = new int[]{j, i}; break; } seen.put(a[i], i); }
check(Arrays.equals(ans, new int[]{0, 1}), "two-sum");
Deque<Character> st = new ArrayDeque<>(); boolean ok = true;
for (char c : "{[()]}".toCharArray()) { if ("([{".indexOf(c) >= 0) st.push(c); else { if (st.isEmpty()) { ok = false; break; } char o = st.pop(); if ("([{".indexOf(o) != ")]}".indexOf(c)) { ok = false; break; } } }
check(ok && st.isEmpty(), "balanced brackets");
LinkedHashMap<Integer, Integer> lru = new LinkedHashMap<>(16, 0.75f, true) { @Override protected boolean removeEldestEntry(Map.Entry<Integer, Integer> e) { return size() > 2; } };
lru.put(1, 1); lru.put(2, 2); lru.get(1); lru.put(3, 3);
check(lru.keySet().equals(Set.of(1, 3)), "LRU evicts key 2");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 90 OK");
    }
}
