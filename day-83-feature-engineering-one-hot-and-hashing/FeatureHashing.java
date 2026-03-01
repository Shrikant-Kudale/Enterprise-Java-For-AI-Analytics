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
 * Day 83 - Feature Engineering: One-Hot and Hashing (short mini-lab: a few lines of code + assertions).
 * Run: javac FeatureHashing.java && java FeatureHashing
 */
public class FeatureHashing {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
List<String> cats = List.of("cat", "dog", "fish"); double[] oh = new double[3]; oh[cats.indexOf("dog")] = 1;
check(Arrays.equals(oh, new double[]{0, 1, 0}), "one-hot");
int dim = 8, idx = Math.floorMod("dog".hashCode(), dim);
check(idx >= 0 && idx < dim && idx == Math.floorMod("dog".hashCode(), dim), "hashing trick is stable and bounded");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 83 OK");
    }
}
