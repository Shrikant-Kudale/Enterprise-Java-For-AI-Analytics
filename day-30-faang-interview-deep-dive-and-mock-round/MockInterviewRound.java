import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/** Day 30 - timed mock round: seven classic problems with assertions. Run: javac MockInterviewRound.java && java MockInterviewRound */
public class MockInterviewRound {
    static final class LRU<K, V> {
        private final class Node { K k; V v; Node prev, next; }
        private final Map<K, Node> map = new HashMap<>(); private final Node head = new Node(), tail = new Node(); private final int cap;
        LRU(int cap) { this.cap = cap; head.next = tail; tail.prev = head; }
        private void unlink(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
        private void pushFront(Node n) { n.next = head.next; n.prev = head; head.next.prev = n; head.next = n; }
        V get(K k) { Node n = map.get(k); if (n == null) return null; unlink(n); pushFront(n); return n.v; }
        void put(K k, V v) {
            Node n = map.get(k);
            if (n != null) { n.v = v; unlink(n); pushFront(n); return; }
            if (map.size() == cap) { Node last = tail.prev; unlink(last); map.remove(last.k); }
            n = new Node(); n.k = k; n.v = v; map.put(k, n); pushFront(n);
        }
    }
    static List<Integer> topKFrequent(int[] a, int k) {
        Map<Integer, Integer> freq = new HashMap<>(); for (int x : a) freq.merge(x, 1, Integer::sum);
        List<List<Integer>> buckets = new ArrayList<>(); for (int i = 0; i <= a.length; i++) buckets.add(new ArrayList<>());
        freq.forEach((x, c) -> buckets.get(c).add(x));
        List<Integer> out = new ArrayList<>();
        for (int c = a.length; c > 0 && out.size() < k; c--) out.addAll(buckets.get(c));
        return out.subList(0, k);
    }
    static int[][] mergeIntervals(int[][] iv) {
        int[][] s = iv.clone(); Arrays.sort(s, Comparator.comparingInt(x -> x[0]));
        List<int[]> out = new ArrayList<>();
        for (int[] x : s) { if (out.isEmpty() || out.get(out.size() - 1)[1] < x[0]) out.add(x.clone()); else out.get(out.size() - 1)[1] = Math.max(out.get(out.size() - 1)[1], x[1]); }
        return out.toArray(new int[0][]);
    }
    static final class MedianFinder {
        private final PriorityQueue<Integer> lo = new PriorityQueue<>(Comparator.reverseOrder()), hi = new PriorityQueue<>();
        void add(int x) { lo.add(x); hi.add(lo.poll()); if (hi.size() > lo.size()) lo.add(hi.poll()); }
        double median() { return lo.size() > hi.size() ? lo.peek() : (lo.peek() + hi.peek()) / 2.0; }
    }
    static int kthLargest(int[] a, int k) {
        int[] x = a.clone(); int target = x.length - k, lo = 0, hi = x.length - 1;
        while (lo < hi) {
            int p = x[(lo + hi) >>> 1], i = lo, j = hi;
            while (i <= j) { while (x[i] < p) i++; while (x[j] > p) j--; if (i <= j) { int t = x[i]; x[i] = x[j]; x[j] = t; i++; j--; } }
            if (target <= j) hi = j; else if (target >= i) lo = i; else break;
        }
        return x[target];
    }
    static final class BoundedQueue<T> {
        private final Object[] items; private int head, tail, count; private final ReentrantLock lock = new ReentrantLock(); private final Condition notFull = lock.newCondition(), notEmpty = lock.newCondition();
        BoundedQueue(int cap) { items = new Object[cap]; }
        void put(T t) throws InterruptedException { lock.lock(); try { while (count == items.length) notFull.await(); items[tail] = t; tail = (tail + 1) % items.length; count++; notEmpty.signal(); } finally { lock.unlock(); } }
        @SuppressWarnings("unchecked") T take() throws InterruptedException { lock.lock(); try { while (count == 0) notEmpty.await(); T t = (T) items[head]; head = (head + 1) % items.length; count--; notFull.signal(); return t; } finally { lock.unlock(); } }
    }
    static final class SlidingWindowLimiter {
        private final Deque<Long> stamps = new ArrayDeque<>(); private final int limit; private final long windowMs;
        SlidingWindowLimiter(int limit, long windowMs) { this.limit = limit; this.windowMs = windowMs; }
        synchronized boolean allow(long now) { while (!stamps.isEmpty() && now - stamps.peekFirst() >= windowMs) stamps.pollFirst(); if (stamps.size() < limit) { stamps.addLast(now); return true; } return false; }
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }
    static long round(String name, Runnable r) { long t = System.nanoTime(); r.run(); long us = (System.nanoTime() - t) / 1000; System.out.printf("  [PASS] %-28s %8d us%n", name, us); return us; }

    public static void main(String[] args) {
        System.out.println("Mock round - 7 problems");
        round("1. LRU cache", () -> { LRU<Integer, Integer> c = new LRU<>(2); c.put(1, 1); c.put(2, 2); c.get(1); c.put(3, 3); check(c.get(2) == null && c.get(1) == 1 && c.get(3) == 3, "lru"); });
        round("2. Top-K frequent", () -> check(new HashSet<>(topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2)).equals(Set.of(1, 2)), "topk"));
        round("3. Merge intervals", () -> { int[][] r = mergeIntervals(new int[][]{{8, 10}, {1, 3}, {2, 6}, {15, 18}}); check(r.length == 3 && r[0][1] == 6, "merge"); });
        round("4. Streaming median", () -> { MedianFinder m = new MedianFinder(); for (int x : new int[]{5, 15, 1, 3}) m.add(x); check(m.median() == 4.0, "median"); });
        round("5. Kth largest (quickselect)", () -> check(kthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2) == 5, "kth"));
        round("6. Bounded blocking queue", () -> {
            try {
                BoundedQueue<Integer> q = new BoundedQueue<>(4); long[] sum = {0};
                Thread prod = new Thread(() -> { try { for (int i = 1; i <= 10_000; i++) q.put(i); } catch (InterruptedException e) { } });
                Thread cons = new Thread(() -> { try { for (int i = 1; i <= 10_000; i++) sum[0] += q.take(); } catch (InterruptedException e) { } });
                prod.start(); cons.start(); prod.join(); cons.join();
                check(sum[0] == 50_005_000L, "producer/consumer sum " + sum[0]);
            } catch (InterruptedException e) { throw new RuntimeException(e); }
        });
        round("7. Sliding-window limiter", () -> { SlidingWindowLimiter l = new SlidingWindowLimiter(2, 1_000); check(l.allow(0) && l.allow(10) && !l.allow(20) && l.allow(1_001), "limiter"); });
        System.out.println("Day 30 OK");
    }
}
