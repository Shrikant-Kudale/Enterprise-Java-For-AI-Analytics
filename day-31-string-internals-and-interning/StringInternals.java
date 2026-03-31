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
 * Day 31 - String Internals and Interning (short mini-lab: a few lines of code + assertions).
 * Run: javac StringInternals.java && java StringInternals
 */
public class StringInternals {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
String a = new String("ml"), b = "ml";
check(a != b && a.equals(b) && a.intern() == b, "intern returns the pooled instance");
StringBuilder sb = new StringBuilder(); for (int i = 0; i < 5; i++) sb.append(i);
check(sb.reverse().toString().equals("43210"), "builder reverse");
check("a,b,,".split(",").length == 2, "trailing empties dropped");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 31 OK");
    }
}
