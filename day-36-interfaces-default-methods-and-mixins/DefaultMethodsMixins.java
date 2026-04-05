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
 * Day 36 - Interfaces, Default Methods and Mixins (short mini-lab: a few lines of code + assertions).
 * Run: javac DefaultMethodsMixins.java && java DefaultMethodsMixins
 */
public class DefaultMethodsMixins {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    interface Greeter { String name(); default String greet() { return "hi " + name(); } }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Greeter g = () -> "bob";
check(g.greet().equals("hi bob"), "default method uses the abstract one");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 36 OK");
    }
}
