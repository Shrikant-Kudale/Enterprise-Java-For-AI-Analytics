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
 * Day 38 - Reflection and Annotations (short mini-lab: a few lines of code + assertions).
 * Run: javac ReflectionAnnotations.java && java ReflectionAnnotations
 */
public class ReflectionAnnotations {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME) @interface Tag { String value(); }
    @Tag("x") public static class Svc { public int f() { return 7; } }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
check(Svc.class.getAnnotation(Tag.class).value().equals("x"), "annotation read");
Object o = Svc.class.getDeclaredConstructor().newInstance();
check((int) Svc.class.getMethod("f").invoke(o) == 7, "reflective call");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 38 OK");
    }
}
