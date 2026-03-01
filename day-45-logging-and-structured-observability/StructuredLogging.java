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
 * Day 45 - Logging and Structured Observability (short mini-lab: a few lines of code + assertions).
 * Run: javac StructuredLogging.java && java StructuredLogging
 */
public class StructuredLogging {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static String log(String lvl, String msg, Map<String, Object> kv) {
        StringBuilder sb = new StringBuilder("{\"level\":\"" + lvl + "\",\"msg\":\"" + msg + "\"");
        new TreeMap<>(kv).forEach((k, v) -> sb.append(",\"").append(k).append("\":").append(v instanceof Number ? String.valueOf(v) : "\"" + v + "\""));
        return sb.append("}").toString();
    }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
String line = log("INFO", "scored", Map.of("ms", 12, "model", "m1"));
check(line.equals("{\"level\":\"INFO\",\"msg\":\"scored\",\"model\":\"m1\",\"ms\":12}"), line);
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 45 OK");
    }
}
