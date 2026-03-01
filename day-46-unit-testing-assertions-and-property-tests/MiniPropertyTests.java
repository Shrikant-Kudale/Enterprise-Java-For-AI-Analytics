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
 * Day 46 - Unit Testing and Property Tests (short mini-lab: a few lines of code + assertions).
 * Run: javac MiniPropertyTests.java && java MiniPropertyTests
 */
public class MiniPropertyTests {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Random r = new Random(1);
for (int n = 0; n < 1000; n++) { int a = r.nextInt(1000), b = r.nextInt(1000); check(a + b == b + a && Math.max(a, b) >= Math.min(a, b), "invariant"); }
try { check(false, "expected"); } catch (AssertionError e) { check(e.getMessage().equals("expected"), "assertion message"); }
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 46 OK");
    }
}
