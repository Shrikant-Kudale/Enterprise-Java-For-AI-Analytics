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
 * Day 59 - Dynamic Programming Patterns (short mini-lab: a few lines of code + assertions).
 * Run: javac DynamicProgrammingPatterns.java && java DynamicProgrammingPatterns
 */
public class DynamicProgrammingPatterns {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int[] coins = {1, 5, 10}; int amt = 27; int[] dp = new int[amt + 1]; Arrays.fill(dp, Integer.MAX_VALUE - 1); dp[0] = 0;
for (int a = 1; a <= amt; a++) for (int c : coins) if (a >= c) dp[a] = Math.min(dp[a], dp[a - c] + 1);
check(dp[amt] == 5, "coin change: 10+10+5+1+1");
String x = "kitten", y = "sitting"; int[][] e = new int[x.length() + 1][y.length() + 1];
for (int i = 0; i <= x.length(); i++) e[i][0] = i; for (int j = 0; j <= y.length(); j++) e[0][j] = j;
for (int i = 1; i <= x.length(); i++) for (int j = 1; j <= y.length(); j++)
    e[i][j] = Math.min(Math.min(e[i - 1][j], e[i][j - 1]) + 1, e[i - 1][j - 1] + (x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1));
check(e[6][7] == 3, "edit distance kitten->sitting");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 59 OK");
    }
}
