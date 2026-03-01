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
 * Day 39 - java.time API and Time Series (short mini-lab: a few lines of code + assertions).
 * Run: javac JavaTimeSeries.java && java JavaTimeSeries
 */
public class JavaTimeSeries {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Instant t = Instant.parse("2026-03-01T00:00:00Z");
ZonedDateTime z = t.atZone(ZoneId.of("Asia/Kolkata"));
check(z.getHour() == 5 && z.getMinute() == 30, "IST offset +05:30");
check(Duration.between(t, t.plusSeconds(90)).toSeconds() == 90, "duration");
check(LocalDate.of(2026, 3, 1).plusDays(91).equals(LocalDate.of(2026, 5, 31)), "Mar-May is 92 days");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 39 OK");
    }
}
