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
 * Day 33 - Immutability and Defensive Copying (short mini-lab: a few lines of code + assertions).
 * Run: javac ImmutabilityCopies.java && java ImmutabilityCopies
 */
public class ImmutabilityCopies {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
List<Integer> src = new ArrayList<>(List.of(1, 2));
List<Integer> copy = List.copyOf(src), view = Collections.unmodifiableList(src);
src.add(3);
check(copy.size() == 2 && view.size() == 3, "copy is a snapshot, view is live");
try { copy.add(9); check(false, "must be immutable"); } catch (UnsupportedOperationException expected) { }
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 33 OK");
    }
}
