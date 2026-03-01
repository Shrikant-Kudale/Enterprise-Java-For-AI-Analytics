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
 * Day 56 - Dijkstra and Shortest Paths (short mini-lab: a few lines of code + assertions).
 * Run: javac DijkstraShortestPath.java && java DijkstraShortestPath
 */
public class DijkstraShortestPath {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int n = 4; int[][] edges = {{0, 1, 1}, {1, 2, 2}, {0, 2, 5}, {2, 3, 1}};
List<List<int[]>> adj = new ArrayList<>(); for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
for (int[] ed : edges) adj.get(ed[0]).add(new int[]{ed[1], ed[2]});
int[] dist = new int[n]; Arrays.fill(dist, Integer.MAX_VALUE); dist[0] = 0;
PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> Integer.compare(x[1], y[1])); pq.add(new int[]{0, 0});
while (!pq.isEmpty()) { int[] c = pq.poll(); if (c[1] > dist[c[0]]) continue; for (int[] ed : adj.get(c[0])) { int nd = c[1] + ed[1]; if (nd < dist[ed[0]]) { dist[ed[0]] = nd; pq.add(new int[]{ed[0], nd}); } } }
check(dist[3] == 4, "shortest path 0->3 is 4");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 56 OK");
    }
}
