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
 * Day 41 - Bit Manipulation and BitSets (short mini-lab: a few lines of code + assertions).
 * Run: javac BitManipulation.java && java BitManipulation
 */
public class BitManipulation {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
check(Integer.bitCount(0b1011) == 3 && Long.numberOfTrailingZeros(64) == 6, "bit counts");
check((8 & (8 - 1)) == 0 && (12 & (12 - 1)) != 0, "power-of-two test");
BitSet bs = new BitSet(); bs.set(3); bs.set(70);
check(bs.cardinality() == 2 && bs.nextSetBit(4) == 70, "bitset");
check((-8 >>> 28) == 15 && (-8 >> 28) == -1, "logical vs arithmetic shift");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 41 OK");
    }
}
