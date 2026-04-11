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
 * Day 42 - Regex and Tokenization (short mini-lab: a few lines of code + assertions).
 * Run: javac RegexTokenization.java && java RegexTokenization
 */
public class RegexTokenization {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
List<String> tok = Arrays.stream("Hello, World! ML-2026".toLowerCase().split("[^a-z0-9]+")).filter(s -> !s.isEmpty()).toList();
check(tok.equals(List.of("hello", "world", "ml", "2026")), "tokens");
var m = java.util.regex.Pattern.compile("(?<k>\\w+)=(?<v>\\d+)").matcher("lr=10");
check(m.matches() && m.group("v").equals("10"), "named groups");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 42 OK");
    }
}
