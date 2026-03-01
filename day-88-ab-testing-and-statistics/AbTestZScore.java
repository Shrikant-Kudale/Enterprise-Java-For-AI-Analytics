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
 * Day 88 - A/B Testing and Statistics (short mini-lab: a few lines of code + assertions).
 * Run: javac AbTestZScore.java && java AbTestZScore
 */
public class AbTestZScore {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double p1 = 0.10, n1 = 1000, p2 = 0.12, n2 = 1000, p = (p1 * n1 + p2 * n2) / (n1 + n2);
double z = (p2 - p1) / Math.sqrt(p * (1 - p) * (1 / n1 + 1 / n2));
check(z > 1.4 && z < 1.5, "z = " + z + " (not significant at 5%)");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 88 OK");
    }
}
