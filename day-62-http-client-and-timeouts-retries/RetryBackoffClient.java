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
 * Day 62 - HTTP Client, Timeouts and Retries (short mini-lab: a few lines of code + assertions).
 * Run: javac RetryBackoffClient.java && java RetryBackoffClient
 */
public class RetryBackoffClient {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
int[] attempts = {0}; Supplier<String> flaky = () -> { if (++attempts[0] < 3) throw new IllegalStateException("503"); return "ok"; };
List<Long> delays = new ArrayList<>(); String r = null;
for (int a = 0; a < 5; a++) { try { r = flaky.get(); break; } catch (IllegalStateException e) { delays.add(100L << a); } }
check("ok".equals(r) && delays.equals(List.of(100L, 200L)), "backoff delays " + delays);
java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://127.0.0.1/")).timeout(Duration.ofSeconds(2)).build();
check(req.timeout().get().toSeconds() == 2, "request timeout configured");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 62 OK");
    }
}
