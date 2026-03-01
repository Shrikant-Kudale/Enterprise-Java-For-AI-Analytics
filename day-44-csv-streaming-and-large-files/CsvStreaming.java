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
 * Day 44 - CSV Streaming and Large Files (short mini-lab: a few lines of code + assertions).
 * Run: javac CsvStreaming.java && java CsvStreaming
 */
public class CsvStreaming {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
String csv = "id,score\n1,0.5\n2,0.7\n";
double sum = new BufferedReader(new StringReader(csv)).lines().skip(1).mapToDouble(l -> Double.parseDouble(l.split(",")[1])).sum();
check(Math.abs(sum - 1.2) < 1e-12, "streamed aggregate");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 44 OK");
    }
}
