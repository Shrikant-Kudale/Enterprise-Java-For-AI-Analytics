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
 * Day 49 - Class Loading Basics (short mini-lab: a few lines of code + assertions).
 * Run: javac ClassLoadingBasics.java && java ClassLoadingBasics
 */
public class ClassLoadingBasics {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
check(Class.forName("java.util.ArrayList").getClassLoader() == null, "core classes come from bootstrap");
check(ClassLoadingBasics.class.getClassLoader() == ClassLoader.getSystemClassLoader(), "app classes from the application loader");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 49 OK");
    }
}
