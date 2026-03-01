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
 * Day 40 - BigDecimal and Numeric Precision (short mini-lab: a few lines of code + assertions).
 * Run: javac BigDecimalPrecision.java && java BigDecimalPrecision
 */
public class BigDecimalPrecision {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
check(0.1 + 0.2 != 0.3, "binary floating point");
check(new BigDecimal("0.1").add(new BigDecimal("0.2")).compareTo(new BigDecimal("0.3")) == 0, "decimal exact");
check(!new BigDecimal("1.10").equals(new BigDecimal("1.1")), "equals checks scale");
check(new BigDecimal("10").divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP).toString().equals("3.33"), "rounded division");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 40 OK");
    }
}
