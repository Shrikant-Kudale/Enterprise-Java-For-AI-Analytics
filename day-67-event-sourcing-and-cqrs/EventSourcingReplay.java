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
 * Day 67 - Event Sourcing and CQRS (short mini-lab: a few lines of code + assertions).
 * Run: javac EventSourcingReplay.java && java EventSourcingReplay
 */
public class EventSourcingReplay {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
record Ev(String type, int amt) { }
List<Ev> log = List.of(new Ev("dep", 100), new Ev("wd", 30), new Ev("dep", 5));
int bal = log.stream().mapToInt(e -> e.type().equals("dep") ? e.amt() : -e.amt()).sum();
check(bal == 75, "state rebuilt by replaying events");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 67 OK");
    }
}
