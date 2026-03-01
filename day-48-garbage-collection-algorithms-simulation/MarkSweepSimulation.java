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
 * Day 48 - Garbage Collection: Mark-Sweep Simulation (short mini-lab: a few lines of code + assertions).
 * Run: javac MarkSweepSimulation.java && java MarkSweepSimulation
 */
public class MarkSweepSimulation {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static final class Obj { final List<Obj> refs = new ArrayList<>(); boolean marked; }
    static void mark(Obj o) { if (o.marked) return; o.marked = true; for (Obj r : o.refs) mark(r); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
List<Obj> heap = new ArrayList<>(); for (int i = 0; i < 5; i++) heap.add(new Obj());
heap.get(0).refs.add(heap.get(1)); heap.get(1).refs.add(heap.get(2));
heap.get(3).refs.add(heap.get(4)); heap.get(4).refs.add(heap.get(3));   // unreachable cycle
mark(heap.get(0)); heap.removeIf(o -> !o.marked);
check(heap.size() == 3, "unreachable cycle collected");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 48 OK");
    }
}
