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
 * Day 43 - JSON Parsing from Scratch (short mini-lab: a few lines of code + assertions).
 * Run: javac JsonParserLite.java && java JsonParserLite
 */
public class JsonParserLite {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static String s; static int pos;
    static void ws() { while (Character.isWhitespace(s.charAt(pos))) pos++; }
    static Object parse() {
        ws(); char c = s.charAt(pos);
        if (c == '[') {
            pos++; List<Object> l = new ArrayList<>(); ws();
            if (s.charAt(pos) == ']') { pos++; return l; }
            while (true) { l.add(parse()); ws(); if (s.charAt(pos++) == ']') return l; }
        }
        if (c == '"') { int j = s.indexOf('"', pos + 1); String r = s.substring(pos + 1, j); pos = j + 1; return r; }
        int j = pos; while (j < s.length() && "-.0123456789eE".indexOf(s.charAt(j)) >= 0) j++;
        double d = Double.parseDouble(s.substring(pos, j)); pos = j; return d;
    }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
s = "[1, 2.5, [\"a\"]]"; pos = 0;
check(parse().equals(List.of(1.0, 2.5, List.of("a"))), "nested arrays");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 43 OK");
    }
}
