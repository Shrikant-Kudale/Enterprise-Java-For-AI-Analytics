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
 * Day 89 - Data Drift: PSI and KS (short mini-lab: a few lines of code + assertions).
 * Run: javac PsiDriftMonitor.java && java PsiDriftMonitor
 */
public class PsiDriftMonitor {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
double[] e = {0.5, 0.3, 0.2}, a = {0.4, 0.4, 0.2}; double psi = 0;
for (int i = 0; i < e.length; i++) psi += (a[i] - e[i]) * Math.log(a[i] / e[i]);
check(psi > 0.04 && psi < 0.06, "PSI = " + psi + " (stable)");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 89 OK");
    }
}
