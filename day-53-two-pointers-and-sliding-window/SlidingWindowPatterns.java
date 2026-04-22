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
 * Day 53 - Two Pointers and Sliding Window (short mini-lab: a few lines of code + assertions).
 * Run: javac SlidingWindowPatterns.java && java SlidingWindowPatterns
 */
public class SlidingWindowPatterns {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int[] a = {2, 1, 5, 1, 3, 2}; int w = 3, sum = 0, best = 0;
for (int i = 0; i < a.length; i++) { sum += a[i]; if (i >= w) sum -= a[i - w]; if (i >= w - 1) best = Math.max(best, sum); }
check(best == 9, "max window sum");
String s = "abcabcbb"; int l = 0, bl = 0; Map<Character, Integer> last = new HashMap<>();
for (int r = 0; r < s.length(); r++) { Integer p = last.put(s.charAt(r), r); if (p != null && p >= l) l = p + 1; bl = Math.max(bl, r - l + 1); }
check(bl == 3, "longest substring without repeats");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 53 OK");
    }
}
