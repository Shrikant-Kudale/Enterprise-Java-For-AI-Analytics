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
 * Day 60 - Backtracking and Combinatorics (short mini-lab: a few lines of code + assertions).
 * Run: javac BacktrackingPermutations.java && java BacktrackingPermutations
 */
public class BacktrackingPermutations {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static void perm(List<Integer> cur, boolean[] used, int n, List<List<Integer>> out) {
        if (cur.size() == n) { out.add(new ArrayList<>(cur)); return; }
        for (int i = 0; i < n; i++) { if (used[i]) continue; used[i] = true; cur.add(i); perm(cur, used, n, out); cur.remove(cur.size() - 1); used[i] = false; }
    }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
List<List<Integer>> out = new ArrayList<>(); perm(new ArrayList<>(), new boolean[4], 4, out);
check(out.size() == 24 && out.get(0).equals(List.of(0, 1, 2, 3)), "4! permutations in lexical order");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 60 OK");
    }
}
