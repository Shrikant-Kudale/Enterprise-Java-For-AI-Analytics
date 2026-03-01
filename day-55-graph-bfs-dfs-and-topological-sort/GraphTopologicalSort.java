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
 * Day 55 - Graph BFS, DFS and Topological Sort (short mini-lab: a few lines of code + assertions).
 * Run: javac GraphTopologicalSort.java && java GraphTopologicalSort
 */
public class GraphTopologicalSort {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int[][] e = {{0, 1}, {0, 2}, {1, 3}, {2, 3}}; Map<Integer, List<Integer>> g = new HashMap<>(); int[] indeg = new int[4];
for (int[] x : e) { g.computeIfAbsent(x[0], k -> new ArrayList<>()).add(x[1]); indeg[x[1]]++; }
Deque<Integer> q = new ArrayDeque<>(); for (int i = 0; i < 4; i++) if (indeg[i] == 0) q.add(i);
List<Integer> order = new ArrayList<>();
while (!q.isEmpty()) { int u = q.poll(); order.add(u); for (int v : g.getOrDefault(u, List.of())) if (--indeg[v] == 0) q.add(v); }
check(order.equals(List.of(0, 1, 2, 3)), "topological order");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 55 OK");
    }
}
