import java.util.*;

/**
 * Day 09 - Binary heap, ring-buffer deque, top-K, k-way merge and sliding-window maximum.
 * Everything is differential-tested against java.util and benchmarked.
 *
 * <p>Run: {@code javac QueuesDequesHeaps.java && java QueuesDequesHeaps}
 */
public class QueuesDequesHeaps {

    // ================================================================= MinHeap
    static final class MinHeap<T> {
        private Object[] a = new Object[16];
        private int size;
        private final Comparator<? super T> cmp;
        MinHeap(Comparator<? super T> cmp) { this.cmp = cmp; }

        void add(T x) {
            if (size == a.length) a = Arrays.copyOf(a, size << 1);
            a[size] = x;
            siftUp(size++);
        }
        @SuppressWarnings("unchecked") T peek() { return size == 0 ? null : (T) a[0]; }
        @SuppressWarnings("unchecked")
        T poll() {
            if (size == 0) return null;
            T top = (T) a[0];
            T last = (T) a[--size];
            a[size] = null;
            if (size > 0) { a[0] = last; siftDown(0); }
            return top;
        }
        int size() { return size; }
        /** Replace the root (used by bounded top-K): O(log n) instead of poll+add. */
        @SuppressWarnings("unchecked")
        void replaceTop(T x) { a[0] = x; siftDown(0); }

        @SuppressWarnings("unchecked")
        private void siftUp(int k) {
            T x = (T) a[k];
            while (k > 0) {
                int parent = (k - 1) >>> 1;
                T e = (T) a[parent];
                if (cmp.compare(x, e) >= 0) break;
                a[k] = e; k = parent;
            }
            a[k] = x;
        }
        @SuppressWarnings("unchecked")
        private void siftDown(int k) {
            T x = (T) a[k];
            int half = size >>> 1;
            while (k < half) {
                int child = 2 * k + 1;
                T c = (T) a[child];
                int right = child + 1;
                if (right < size && cmp.compare(c, (T) a[right]) > 0) c = (T) a[child = right];
                if (cmp.compare(x, c) <= 0) break;
                a[k] = c; k = child;
            }
            a[k] = x;
        }
    }

    // ================================================================= RingDeque (ArrayDeque clone)
    static final class RingDeque<E> {
        private Object[] elems = new Object[16];
        private int head, tail;
        void addFirst(E e) {
            head = (head - 1) & (elems.length - 1);
            elems[head] = Objects.requireNonNull(e);
            if (head == tail) grow();
        }
        void addLast(E e) {
            elems[tail] = Objects.requireNonNull(e);
            tail = (tail + 1) & (elems.length - 1);
            if (tail == head) grow();
        }
        @SuppressWarnings("unchecked")
        E pollFirst() {
            if (head == tail) return null;
            E e = (E) elems[head]; elems[head] = null;
            head = (head + 1) & (elems.length - 1);
            return e;
        }
        @SuppressWarnings("unchecked")
        E pollLast() {
            if (head == tail) return null;
            tail = (tail - 1) & (elems.length - 1);
            E e = (E) elems[tail]; elems[tail] = null;
            return e;
        }
        @SuppressWarnings("unchecked") E peekFirst() { return head == tail ? null : (E) elems[head]; }
        @SuppressWarnings("unchecked") E peekLast() { return head == tail ? null : (E) elems[(tail - 1) & (elems.length - 1)]; }
        boolean isEmpty() { return head == tail; }
        int size() { return (tail - head) & (elems.length - 1); }
        private void grow() { // called only when full (head == tail)
            int n = elems.length, right = n - head;
            Object[] bigger = new Object[n << 1];
            System.arraycopy(elems, head, bigger, 0, right);
            System.arraycopy(elems, 0, bigger, right, head);
            elems = bigger; head = 0; tail = n;
        }
    }

    // ================================================================= algorithms
    /** K largest values, descending, using a size-K min-heap: O(n log k). */
    static int[] topK(int[] data, int k) {
        MinHeap<Integer> heap = new MinHeap<>(Integer::compare);
        for (int x : data) {
            if (heap.size() < k) heap.add(x);
            else if (x > heap.peek()) heap.replaceTop(x);
        }
        int[] out = new int[heap.size()];
        for (int i = out.length - 1; i >= 0; i--) out[i] = heap.poll();
        return out;
    }

    record Cursor(int run, int idx, int value) {}

    /** Merge K sorted int arrays into one sorted array: O(N log K). */
    static int[] kWayMerge(List<int[]> runs) {
        int total = runs.stream().mapToInt(r -> r.length).sum();
        MinHeap<Cursor> heap = new MinHeap<>(Comparator.comparingInt(Cursor::value));
        for (int i = 0; i < runs.size(); i++) if (runs.get(i).length > 0) heap.add(new Cursor(i, 0, runs.get(i)[0]));
        int[] out = new int[total];
        for (int o = 0; o < total; o++) {
            Cursor c = heap.poll();
            out[o] = c.value();
            int next = c.idx() + 1;
            if (next < runs.get(c.run()).length) heap.add(new Cursor(c.run(), next, runs.get(c.run())[next]));
        }
        return out;
    }

    /** Sliding-window maximum with a monotonic deque of indices: amortised O(n). */
    static int[] slidingMax(int[] a, int w) {
        int[] out = new int[a.length - w + 1];
        RingDeque<Integer> dq = new RingDeque<>();
        for (int i = 0; i < a.length; i++) {
            while (!dq.isEmpty() && a[dq.peekLast()] <= a[i]) dq.pollLast();
            dq.addLast(i);
            if (dq.peekFirst() <= i - w) dq.pollFirst();
            if (i >= w - 1) out[i - w + 1] = a[dq.peekFirst()];
        }
        return out;
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        Random rnd = new Random(11);

        // ---- heap differential test (min and max via reversed comparator)
        MinHeap<Integer> heap = new MinHeap<>(Integer::compare);
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int i = 0; i < 100_000; i++) {
            if (rnd.nextInt(3) > 0 || pq.isEmpty()) { int v = rnd.nextInt(1_000_000); heap.add(v); pq.add(v); }
            else check(heap.poll().equals(pq.poll()), "poll order");
        }
        while (!pq.isEmpty()) check(heap.poll().equals(pq.poll()), "drain order");
        check(heap.poll() == null && heap.peek() == null, "empty heap");
        MinHeap<Integer> max = new MinHeap<>(Comparator.reverseOrder());
        for (int x : new int[]{3, 9, 1, 7}) max.add(x);
        check(max.poll() == 9 && max.poll() == 7, "max-heap via reversed comparator");

        // ---- ring deque differential test against ArrayDeque
        RingDeque<Integer> ring = new RingDeque<>();
        ArrayDeque<Integer> jdk = new ArrayDeque<>();
        for (int i = 0; i < 200_000; i++) {
            switch (rnd.nextInt(4)) {
                case 0 -> { ring.addFirst(i); jdk.addFirst(i); }
                case 1 -> { ring.addLast(i); jdk.addLast(i); }
                case 2 -> check(Objects.equals(ring.pollFirst(), jdk.pollFirst()), "pollFirst");
                default -> check(Objects.equals(ring.pollLast(), jdk.pollLast()), "pollLast");
            }
            check(ring.size() == jdk.size(), "size");
        }
        check(Objects.equals(ring.peekFirst(), jdk.peekFirst()) && Objects.equals(ring.peekLast(), jdk.peekLast()), "peeks");

        // ---- top-K, k-way merge, sliding max
        int[] data = rnd.ints(300_000, 0, 10_000_000).toArray();
        int[] sorted = data.clone(); Arrays.sort(sorted);
        int[] top = topK(data, 10);
        for (int i = 0; i < 10; i++) check(top[i] == sorted[sorted.length - 1 - i], "topK[" + i + "]");

        List<int[]> runs = new ArrayList<>();
        for (int r = 0; r < 50; r++) { int[] run = rnd.ints(rnd.nextInt(2_000), 0, 1_000_000).sorted().toArray(); runs.add(run); }
        int[] merged = kWayMerge(runs);
        int[] expect = runs.stream().flatMapToInt(Arrays::stream).sorted().toArray();
        check(Arrays.equals(merged, expect), "k-way merge");

        int[] series = rnd.ints(5_000, 0, 1_000).toArray();
        int[] fast = slidingMax(series, 25), naive = new int[series.length - 24];
        for (int i = 0; i < naive.length; i++) { int m = Integer.MIN_VALUE; for (int j = i; j < i + 25; j++) m = Math.max(m, series[j]); naive[i] = m; }
        check(Arrays.equals(fast, naive), "sliding window maximum");

        // ---- benchmarks
        int n = 500_000;
        int[] big = rnd.ints(n, 0, Integer.MAX_VALUE).toArray();
        long sink = 0;
        for (int w = 0; w < 3; w++) { sink += heapSort(big); sink += pqSort(big); }
        long t0 = System.nanoTime(); sink += heapSort(big);
        long t1 = System.nanoTime(); sink += pqSort(big);
        long t2 = System.nanoTime();
        System.out.printf("sort %d via heap: custom MinHeap %.0f ms | PriorityQueue %.0f ms%n", n, (t1 - t0) / 1e6, (t2 - t1) / 1e6);

        int[] huge = rnd.ints(5_000_000, 0, Integer.MAX_VALUE).toArray();
        t0 = System.nanoTime(); int[] viaHeap = topK(huge, 10);
        t1 = System.nanoTime(); int[] full = huge.clone(); Arrays.sort(full);
        t2 = System.nanoTime();
        check(viaHeap[0] == full[full.length - 1], "topK agrees with full sort");
        System.out.printf("top-10 of 5M: heap %.0f ms | full sort %.0f ms%n", (t1 - t0) / 1e6, (t2 - t1) / 1e6);

        int ops = 5_000_000;
        t0 = System.nanoTime(); ArrayDeque<Integer> ad = new ArrayDeque<>(); for (int i = 0; i < ops; i++) { ad.addLast(i); if ((i & 3) == 3) { ad.pollFirst(); ad.pollFirst(); } }
        t1 = System.nanoTime(); LinkedList<Integer> ll = new LinkedList<>(); for (int i = 0; i < ops; i++) { ll.addLast(i); if ((i & 3) == 3) { ll.pollFirst(); ll.pollFirst(); } }
        t2 = System.nanoTime(); RingDeque<Integer> rd = new RingDeque<>(); for (int i = 0; i < ops; i++) { rd.addLast(i); if ((i & 3) == 3) { rd.pollFirst(); rd.pollFirst(); } }
        long t3 = System.nanoTime();
        System.out.printf("queue churn x5M: ArrayDeque %.0f ms | LinkedList %.0f ms | RingDeque %.0f ms (sink=%d)%n",
                (t1 - t0) / 1e6, (t2 - t1) / 1e6, (t3 - t2) / 1e6, sink + ad.size() + ll.size() + rd.size());
        System.out.println("Day 09 OK");
    }

    static long heapSort(int[] a) {
        MinHeap<Integer> h = new MinHeap<>(Integer::compare);
        for (int x : a) h.add(x);
        long s = 0; Integer v; int i = 0;
        while ((v = h.poll()) != null) s += (long) v * (i++ & 7);
        return s;
    }
    static long pqSort(int[] a) {
        PriorityQueue<Integer> h = new PriorityQueue<>();
        for (int x : a) h.add(x);
        long s = 0; Integer v; int i = 0;
        while ((v = h.poll()) != null) s += (long) v * (i++ & 7);
        return s;
    }
}
