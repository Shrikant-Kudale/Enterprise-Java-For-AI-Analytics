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
 * Day 66 - Message Queues and Consumer Groups (short mini-lab: a few lines of code + assertions).
 * Run: javac PartitionedLogToy.java && java PartitionedLogToy
 */
public class PartitionedLogToy {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
List<List<String>> parts = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
for (String k : List.of("u1", "u2", "u1", "u3", "u1")) parts.get(Math.floorMod(k.hashCode(), 3)).add(k);
check(parts.stream().anyMatch(p -> p.equals(List.of("u1", "u1", "u1"))), "same key lands in one partition (ordering kept)");
int[] offsets = new int[3]; offsets[1] += 3; check(offsets[1] == 3, "consumer offset advances");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 66 OK");
    }
}
