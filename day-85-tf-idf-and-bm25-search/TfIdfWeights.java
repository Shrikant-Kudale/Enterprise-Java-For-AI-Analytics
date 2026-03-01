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
 * Day 85 - TF-IDF and BM25 Search (short mini-lab: a few lines of code + assertions).
 * Run: javac TfIdfWeights.java && java TfIdfWeights
 */
public class TfIdfWeights {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
String[] docs = {"java streams", "java java concurrency", "python pandas"}; double n = 3;
long df = Arrays.stream(docs).filter(d -> d.contains("java")).count(); double idf = Math.log(n / df);
check(df == 2 && 2 * idf > 1 * idf, "more occurrences weigh more");
check(Math.log(n / 1) > idf, "rarer terms get a higher idf");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 85 OK");
    }
}
