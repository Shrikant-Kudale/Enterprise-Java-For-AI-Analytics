import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * Day 08 - A HashMap re-implementation (spread, power-of-two index, resize) plus hash-collision
 * behaviour of java.util.HashMap and ConcurrentHashMap atomic operations under contention.
 *
 * <p>Run: {@code javac HashMapInternals.java && java HashMapInternals}
 */
public class HashMapInternals {

    // ===================================================================== SimpleHashMap
    static final class SimpleHashMap<K, V> {
        static final class Node<K, V> {
            final int hash; final K key; V value; Node<K, V> next;
            Node(int hash, K key, V value) { this.hash = hash; this.key = key; this.value = value; }
        }
        private Node<K, V>[] table;
        private int size, threshold, resizes;
        private static final float LOAD_FACTOR = 0.75f;
        static final int TREEIFY_THRESHOLD = 8;

        static int spread(int h) { return h ^ (h >>> 16); }

        @SuppressWarnings("unchecked")
        private void resize() {
            Node<K, V>[] old = table;
            int newCap = old == null ? 16 : old.length << 1;
            Node<K, V>[] fresh = (Node<K, V>[]) new Node[newCap];
            threshold = (int) (newCap * LOAD_FACTOR);
            if (old != null) {
                resizes++;
                for (Node<K, V> head : old)
                    for (Node<K, V> e = head; e != null; ) {
                        Node<K, V> next = e.next;
                        e.next = null;
                        append(fresh, e);
                        e = next;
                    }
            }
            table = fresh;
        }
        private static <K, V> void append(Node<K, V>[] tab, Node<K, V> n) {
            int i = (tab.length - 1) & n.hash;
            if (tab[i] == null) { tab[i] = n; return; }
            Node<K, V> p = tab[i];
            while (p.next != null) p = p.next;
            p.next = n; // tail insertion keeps relative order like java.util.HashMap
        }

        V put(K key, V value) {
            if (table == null) resize();
            int h = spread(Objects.hashCode(key));
            for (Node<K, V> n = table[(table.length - 1) & h]; n != null; n = n.next)
                if (n.hash == h && (n.key == key || (key != null && key.equals(n.key)))) {
                    V old = n.value; n.value = value; return old;
                }
            append(table, new Node<>(h, key, value));
            if (++size > threshold) resize();
            return null;
        }
        V get(Object key) {
            if (table == null) return null;
            int h = spread(Objects.hashCode(key));
            for (Node<K, V> n = table[(table.length - 1) & h]; n != null; n = n.next)
                if (n.hash == h && (n.key == key || (key != null && key.equals(n.key)))) return n.value;
            return null;
        }
        int size() { return size; }
        int capacity() { return table == null ? 0 : table.length; }
        int resizes() { return resizes; }
        int maxChain() {
            int max = 0;
            if (table == null) return 0;
            for (Node<K, V> head : table) { int c = 0; for (Node<K, V> e = head; e != null; e = e.next) c++; max = Math.max(max, c); }
            return max;
        }
    }

    // ===================================================================== keys with controlled hashCodes
    static class BadKey {          // constant hash, NOT Comparable
        final int id; BadKey(int id) { this.id = id; }
        @Override public int hashCode() { return 42; }
        @Override public boolean equals(Object o) { return o instanceof BadKey b && b.id == id; }
    }
    static final class BadComparableKey extends BadKey implements Comparable<BadKey> {
        BadComparableKey(int id) { super(id); }
        @Override public int compareTo(BadKey o) { return Integer.compare(id, o.id); }
    }
    static final class GoodKey extends BadKey {
        GoodKey(int id) { super(id); }
        @Override public int hashCode() { return Integer.hashCode(id) * 0x9E3779B1; }
    }

    static long timeLookups(Map<BadKey, Integer> m, List<? extends BadKey> keys) {
        long sink = 0, t0 = System.nanoTime();
        for (BadKey k : keys) sink += m.get(k);
        long t = System.nanoTime() - t0;
        if (sink < 0) throw new AssertionError();
        return t;
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        // ---- our map vs java.util.HashMap (differential)
        SimpleHashMap<Integer, Integer> mine = new SimpleHashMap<>();
        Map<Integer, Integer> jdk = new HashMap<>();
        Random rnd = new Random(3);
        for (int i = 0; i < 200_000; i++) {
            int k = rnd.nextInt(100_000), v = rnd.nextInt();
            check(Objects.equals(mine.put(k, v), jdk.put(k, v)), "put return value");
        }
        check(mine.size() == jdk.size(), "size");
        for (Map.Entry<Integer, Integer> e : jdk.entrySet()) check(mine.get(e.getKey()).equals(e.getValue()), "get");
        check(mine.get(-1) == null, "absent");
        System.out.printf("SimpleHashMap: size=%d capacity=%d resizes=%d maxChain=%d%n",
                mine.size(), mine.capacity(), mine.resizes(), mine.maxChain());
        check((mine.capacity() & (mine.capacity() - 1)) == 0 && mine.size() <= 0.75 * mine.capacity() && mine.size() > 0.75 * (mine.capacity() / 2), "power-of-two capacity respecting load factor");
        check(mine.maxChain() < 16, "good hash keeps chains short");

        // null key support (hash 0 -> bucket 0)
        SimpleHashMap<String, String> nm = new SimpleHashMap<>();
        nm.put(null, "n"); check("n".equals(nm.get(null)), "null key");

        // degenerate hash: all keys in one chain => O(n) lookups
        SimpleHashMap<BadKey, Integer> degenerate = new SimpleHashMap<>();
        for (int i = 0; i < 1_000; i++) degenerate.put(new BadKey(i), i);
        check(degenerate.maxChain() == 1_000, "constant hashCode => single chain");

        // spread() demonstration
        check(SimpleHashMap.spread(0x10000) == (0x10000 ^ 1), "high bits folded into low bits");

        // ---- java.util.HashMap treeification effect (n large enough to show O(n) vs O(log n))
        final int N = 6_000;
        List<GoodKey> good = new ArrayList<>(); List<BadKey> bad = new ArrayList<>(); List<BadComparableKey> cmp = new ArrayList<>();
        Map<BadKey, Integer> mGood = new HashMap<>(), mBad = new HashMap<>(), mCmp = new HashMap<>();
        for (int i = 0; i < N; i++) {
            GoodKey g = new GoodKey(i); good.add(g); mGood.put(g, i);
            BadKey b = new BadKey(i); bad.add(b); mBad.put(b, i);
            BadComparableKey c = new BadComparableKey(i); cmp.add(c); mCmp.put(c, i);
        }
        for (int w = 0; w < 3; w++) { timeLookups(mGood, good); timeLookups(mBad, bad); timeLookups(mCmp, cmp); }
        long tGood = timeLookups(mGood, good), tBad = timeLookups(mBad, bad), tCmp = timeLookups(mCmp, cmp);
        System.out.printf("lookup x%d  good-hash: %.2f ms | all-collide NON-comparable: %.2f ms | all-collide Comparable (tree): %.2f ms%n",
                N, tGood / 1e6, tBad / 1e6, tCmp / 1e6);
        check(mBad.get(bad.get(N - 1)) == N - 1 && mCmp.get(cmp.get(N / 2)) == N / 2, "lookups still correct");

        // ---- ConcurrentHashMap: atomic merge under contention
        final int THREADS = 8, PER = 100_000, KEYS = 1_000;
        ConcurrentHashMap<Integer, Long> counts = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, LongAdder> adders = new ConcurrentHashMap<>();
        Map<Integer, Long> sync = Collections.synchronizedMap(new HashMap<>());
        AtomicInteger loads = new AtomicInteger();
        ConcurrentHashMap<Integer, String> lazy = new ConcurrentHashMap<>();

        long tMerge = runThreads(THREADS, PER, i -> counts.merge(i % KEYS, 1L, Long::sum));
        long tAdder = runThreads(THREADS, PER, i -> adders.computeIfAbsent(i % KEYS, k -> new LongAdder()).increment());
        long tSync = runThreads(THREADS, PER, i -> sync.merge(i % KEYS, 1L, Long::sum));
        runThreads(THREADS, PER, i -> lazy.computeIfAbsent(i % KEYS, k -> { loads.incrementAndGet(); return "v" + k; }));

        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        check(total == (long) THREADS * PER, "CHM.merge must not lose updates, got " + total);
        check(adders.values().stream().mapToLong(LongAdder::sum).sum() == (long) THREADS * PER, "LongAdder values");
        check(sync.values().stream().mapToLong(Long::longValue).sum() == (long) THREADS * PER, "synchronizedMap");
        check(counts.get(0) == (long) THREADS * PER / KEYS, "per-key count");
        check(loads.get() == KEYS, "computeIfAbsent must invoke the loader exactly once per key, got " + loads.get());
        check(counts.mappingCount() == KEYS, "mappingCount");
        try { counts.put(null, 1L); check(false, "null key must be rejected"); } catch (NullPointerException ok) { }

        System.out.printf("8 threads x 100k increments -> CHM.merge %.0f ms | CHM+LongAdder %.0f ms | synchronizedMap %.0f ms%n",
                tMerge / 1e6, tAdder / 1e6, tSync / 1e6);
        System.out.println("Day 08 OK");
    }

    static long runThreads(int threads, int perThread, java.util.function.IntConsumer body) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<?>> fs = new ArrayList<>();
        for (int t = 0; t < threads; t++)
            fs.add(pool.submit(() -> { start.await(); for (int i = 0; i < perThread; i++) body.accept(i); return null; }));
        long t0 = System.nanoTime();
        start.countDown();
        for (Future<?> f : fs) f.get();
        long t = System.nanoTime() - t0;
        pool.shutdown();
        return t;
    }
}
