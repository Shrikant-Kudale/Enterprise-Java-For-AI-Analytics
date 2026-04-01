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
 * Day 32 - Optional and Null Safety (short mini-lab: a few lines of code + assertions).
 * Run: javac OptionalNullSafety.java && java OptionalNullSafety
 */
public class OptionalNullSafety {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Optional<String> o = Optional.of(" x ").map(String::strip).filter(s -> !s.isEmpty());
check(o.orElse("?").equals("x"), "map/filter");
check(Optional.<Integer>empty().or(() -> Optional.of(1)).get() == 1, "or() fallback");
check(Optional.ofNullable((String) null).map(String::length).isEmpty(), "null -> empty");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 32 OK");
    }
}
