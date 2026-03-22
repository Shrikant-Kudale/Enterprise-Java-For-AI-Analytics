import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.concurrent.locks.LockSupport;

/** Day 22 - TTL+LRU cache, single-flight, RESP codec, Zipf hit ratios. Run: javac CacheAndResp.java && java CacheAndResp */
public class CacheAndResp {
    static final class TtlLruCache<K, V> {
        private record Entry<V>(V value, long expiresAt) { }
        private final LinkedHashMap<K, Entry<V>> map;
        private final long ttlMs; private final LongSupplier clock;
        long hits, misses;
        TtlLruCache(int capacity, long ttlMs, LongSupplier clock) {
            this.ttlMs = ttlMs; this.clock = clock;
            this.map = new LinkedHashMap<>(16, 0.75f, true) {
                @Override protected boolean removeEldestEntry(Map.Entry<K, Entry<V>> e) { return size() > capacity; }
            };
        }
        synchronized V get(K key) {
            Entry<V> e = map.get(key);
            if (e == null || e.expiresAt() <= clock.getAsLong()) { if (e != null) map.remove(key); misses++; return null; }
            hits++;
            return e.value();
        }
        synchronized void put(K key, V v) { map.put(key, new Entry<>(v, clock.getAsLong() + ttlMs)); }
        synchronized int size() { return map.size(); }
    }

    /** Only one loader runs per key; concurrent callers share its result. */
    static final class SingleFlight<K, V> {
        private final ConcurrentHashMap<K, CompletableFuture<V>> inflight = new ConcurrentHashMap<>();
        V load(K key, Function<K, V> loader) {
            CompletableFuture<V> f = inflight.computeIfAbsent(key, k -> CompletableFuture.supplyAsync(() -> loader.apply(k)));
            try { return f.join(); } finally { inflight.remove(key, f); }
        }
    }

    // ---------------------------------------------------------------- RESP codec
    static byte[] encode(String... args) {
        StringBuilder sb = new StringBuilder("*" + args.length + "\r\n");
        for (String a : args) sb.append('$').append(a.getBytes(StandardCharsets.UTF_8).length).append("\r\n").append(a).append("\r\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
    static String line(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder(); int c;
        while ((c = in.read()) != '\r') sb.append((char) c);
        in.read();
        return sb.toString();
    }
    static Object decode(InputStream in) throws IOException {
        int type = in.read();
        switch (type) {
            case '+': return line(in);
            case '-': return new RuntimeException(line(in));
            case ':': return Long.parseLong(line(in));
            case '$': { int len = Integer.parseInt(line(in)); if (len < 0) return null; byte[] b = in.readNBytes(len); in.readNBytes(2); return new String(b, StandardCharsets.UTF_8); }
            case '*': { int n = Integer.parseInt(line(in)); List<Object> l = new ArrayList<>(); for (int i = 0; i < n; i++) l.add(decode(in)); return l; }
            default: throw new IOException("bad RESP type " + (char) type);
        }
    }
    static Object parse(String s) throws IOException { return decode(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8))); }

    static double hitRatio(int cacheSize, int keys, int requests) {
        double[] cdf = new double[keys]; double sum = 0;
        for (int i = 0; i < keys; i++) { sum += 1.0 / (i + 1); cdf[i] = sum; }
        Random r = new Random(1);
        TtlLruCache<Integer, Integer> c = new TtlLruCache<>(cacheSize, Long.MAX_VALUE / 2, System::currentTimeMillis);
        for (int i = 0; i < requests; i++) {
            int k = Arrays.binarySearch(cdf, r.nextDouble() * sum);
            if (k < 0) k = -k - 1;
            if (c.get(k) == null) c.put(k, k);
        }
        return c.hits / (double) (c.hits + c.misses);
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long[] now = {0};
        TtlLruCache<String, String> cache = new TtlLruCache<>(2, 1_000, () -> now[0]);
        cache.put("a", "1"); cache.put("b", "2"); cache.get("a"); cache.put("c", "3");
        check(cache.get("b") == null && cache.get("a") != null, "LRU evicts least recently used (b)");
        now[0] = 1_500;
        check(cache.get("a") == null, "TTL expired with fake clock");

        AtomicInteger loads = new AtomicInteger();
        SingleFlight<String, String> sf = new SingleFlight<>();
        ExecutorService ex = Executors.newFixedThreadPool(32);
        CountDownLatch go = new CountDownLatch(1);
        List<Future<String>> fs = new ArrayList<>();
        for (int i = 0; i < 32; i++) fs.add(ex.submit(() -> { go.await(); return sf.load("hot", k -> { loads.incrementAndGet(); LockSupport.parkNanos(300_000_000L); return "v"; }); }));
        go.countDown();
        for (Future<String> f : fs) check(f.get().equals("v"), "value");
        check(loads.get() <= 2, "stampede collapsed to " + loads.get() + " load(s)");
        ex.shutdown();

        check(new String(encode("SET", "k", "v")).equals("*3\r\n$3\r\nSET\r\n$1\r\nk\r\n$1\r\nv\r\n"), "RESP encode");
        check(parse("+OK\r\n").equals("OK") && parse(":42\r\n").equals(42L) && parse("$5\r\nhello\r\n").equals("hello") && parse("$-1\r\n") == null, "RESP scalars");
        check(parse("*2\r\n$1\r\na\r\n:7\r\n").equals(List.of("a", 7L)), "RESP array");

        double small = hitRatio(100, 10_000, 200_000), mid = hitRatio(1_000, 10_000, 200_000), big = hitRatio(5_000, 10_000, 200_000);
        System.out.printf("Zipf hit ratio: cache 100 -> %.2f | 1000 -> %.2f | 5000 -> %.2f%n", small, mid, big);
        check(small < mid && mid < big, "bigger cache => higher hit ratio");
        System.out.println("Day 22 OK");
    }
}
