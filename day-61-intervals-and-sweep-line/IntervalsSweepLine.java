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
 * Day 61 - Intervals and Sweep Line (short mini-lab: a few lines of code + assertions).
 * Run: javac IntervalsSweepLine.java && java IntervalsSweepLine
 */
public class IntervalsSweepLine {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int[][] iv = {{1, 3}, {2, 6}, {8, 10}, {9, 12}}; List<int[]> m = new ArrayList<>();
for (int[] x : iv) { if (m.isEmpty() || m.get(m.size() - 1)[1] < x[0]) m.add(x.clone()); else m.get(m.size() - 1)[1] = Math.max(m.get(m.size() - 1)[1], x[1]); }
check(m.size() == 2 && m.get(1)[1] == 12, "merged");
TreeMap<Integer, Integer> ev = new TreeMap<>(); for (int[] x : iv) { ev.merge(x[0], 1, Integer::sum); ev.merge(x[1], -1, Integer::sum); }
int cur = 0, max = 0; for (int d : ev.values()) { cur += d; max = Math.max(max, cur); }
check(max == 2, "max concurrency");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 61 OK");
    }
}
