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
 * Day 75 - Leader Election and Heartbeats (short mini-lab: a few lines of code + assertions).
 * Run: javac HeartbeatLeaderElection.java && java HeartbeatLeaderElection
 */
public class HeartbeatLeaderElection {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Map<String, Long> lastBeat = new TreeMap<>(Map.of("n1", 0L, "n2", 900L, "n3", 950L)); long now = 1000, timeout = 500;
String leader = lastBeat.entrySet().stream().filter(e -> now - e.getValue() < timeout).map(Map.Entry::getKey).findFirst().orElse("none");
check(leader.equals("n2"), "lowest-id alive node leads (n1 timed out)");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 75 OK");
    }
}
