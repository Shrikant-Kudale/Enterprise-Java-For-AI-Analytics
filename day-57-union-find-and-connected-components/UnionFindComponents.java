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
 * Day 57 - Union-Find and Connected Components (short mini-lab: a few lines of code + assertions).
 * Run: javac UnionFindComponents.java && java UnionFindComponents
 */
public class UnionFindComponents {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static int[] parent;
    static int find(int x) { return parent[x] == x ? x : (parent[x] = find(parent[x])); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
parent = new int[6]; for (int i = 0; i < 6; i++) parent[i] = i;
for (int[] x : new int[][]{{0, 1}, {1, 2}, {3, 4}}) parent[find(x[0])] = find(x[1]);
Set<Integer> roots = new HashSet<>(); for (int i = 0; i < 6; i++) roots.add(find(i));
check(roots.size() == 3, "components: {0,1,2},{3,4},{5}");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 57 OK");
    }
}
