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
 * Day 58 - Tries and Autocomplete (short mini-lab: a few lines of code + assertions).
 * Run: javac TrieAutocomplete.java && java TrieAutocomplete
 */
public class TrieAutocomplete {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static final class Trie {
        final Map<Character, Trie> kids = new TreeMap<>(); boolean end;
        void add(String w) { Trie t = this; for (char c : w.toCharArray()) t = t.kids.computeIfAbsent(c, k -> new Trie()); t.end = true; }
        List<String> withPrefix(String p) {
            Trie t = this; for (char c : p.toCharArray()) { t = t.kids.get(c); if (t == null) return List.of(); }
            List<String> out = new ArrayList<>(); t.collect(new StringBuilder(p), out); return out;
        }
        void collect(StringBuilder sb, List<String> out) {
            if (end) out.add(sb.toString());
            for (var e : kids.entrySet()) { sb.append(e.getKey()); e.getValue().collect(sb, out); sb.deleteCharAt(sb.length() - 1); }
        }
    }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
Trie t = new Trie(); for (String w : List.of("car", "cat", "cart", "dog")) t.add(w);
check(t.withPrefix("ca").equals(List.of("car", "cart", "cat")), "autocomplete in lexical order");
check(t.withPrefix("x").isEmpty(), "no match");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 58 OK");
    }
}
