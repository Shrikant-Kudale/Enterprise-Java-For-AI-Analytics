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
 * Day 34 - equals, hashCode and Object Contracts (short mini-lab: a few lines of code + assertions).
 * Run: javac EqualsHashCodeContract.java && java EqualsHashCodeContract
 */
public class EqualsHashCodeContract {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static final class Pt {
        final int x, y; Pt(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object o) { return o instanceof Pt p && p.x == x && p.y == y; }
        @Override public int hashCode() { return Objects.hash(x, y); }
    }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Set<Pt> s = new HashSet<>(); s.add(new Pt(1, 2));
check(s.contains(new Pt(1, 2)), "equal keys found");
check(new Pt(1, 2).hashCode() == new Pt(1, 2).hashCode(), "equal objects, equal hash");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 34 OK");
    }
}
