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
 * Day 81 - Naive Bayes Text Classifier (short mini-lab: a few lines of code + assertions).
 * Run: javac NaiveBayesLite.java && java NaiveBayesLite
 */
public class NaiveBayesLite {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
String[] docs = {"great fun", "great", "awful bad", "bad"}; int[] lab = {1, 1, 0, 0}; Map<String, int[]> cnt = new HashMap<>();
for (int i = 0; i < docs.length; i++) for (String w : docs[i].split(" ")) cnt.computeIfAbsent(w, k -> new int[2])[lab[i]]++;
int[] tot = new int[2]; for (int[] c : cnt.values()) { tot[0] += c[0]; tot[1] += c[1]; } int V = cnt.size();
double pos = Math.log((cnt.get("great")[1] + 1.0) / (tot[1] + V)), neg = Math.log((cnt.get("great")[0] + 1.0) / (tot[0] + V));
check(pos > neg && !Double.isInfinite(neg), "'great' favours positive; smoothing keeps log finite");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 81 OK");
    }
}
