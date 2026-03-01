import java.util.*;

/**
 * Day 07 - ArrayList and LinkedList rebuilt from scratch, validated against java.util and benchmarked.
 *
 * <p>Run: {@code javac ListInternals.java && java ListInternals}
 */
public class ListInternals {

    // =================================================================== SimpleArrayList
    static final class SimpleArrayList<E> implements Iterable<E> {
        private Object[] data = new Object[10];
        private int size;
        private int modCount;
        final List<Integer> growthHistory = new ArrayList<>();

        void add(E e) {
            if (size == data.length) grow();
            data[size++] = e;
            modCount++;
        }
        void add(int index, E e) {
            if (index < 0 || index > size) throw new IndexOutOfBoundsException(index);
            if (size == data.length) grow();
            System.arraycopy(data, index, data, index + 1, size - index);
            data[index] = e;
            size++;
            modCount++;
        }
        @SuppressWarnings("unchecked")
        E get(int i) { Objects.checkIndex(i, size); return (E) data[i]; }
        @SuppressWarnings("unchecked")
        E remove(int i) {
            Objects.checkIndex(i, size);
            E old = (E) data[i];
            int moved = size - i - 1;
            if (moved > 0) System.arraycopy(data, i + 1, data, i, moved);
            data[--size] = null; // avoid loitering
            modCount++;
            return old;
        }
        int size() { return size; }
        int capacity() { return data.length; }

        private void grow() {
            int newCap = data.length + (data.length >> 1);
            if (newCap < 0) throw new OutOfMemoryError("capacity overflow");
            data = Arrays.copyOf(data, newCap);
            growthHistory.add(newCap);
        }

        @Override public Iterator<E> iterator() {
            return new Iterator<>() {
                int cursor = 0, expected = modCount;
                public boolean hasNext() { return cursor != size; }
                public E next() {
                    if (modCount != expected) throw new ConcurrentModificationException();
                    if (cursor >= size) throw new NoSuchElementException();
                    return get(cursor++);
                }
            };
        }
    }

    // =================================================================== SimpleLinkedList
    static final class SimpleLinkedList<E> {
        private static final class Node<E> {
            E item; Node<E> prev, next;
            Node(Node<E> prev, E item, Node<E> next) { this.prev = prev; this.item = item; this.next = next; }
        }
        private Node<E> first, last;
        private int size;

        void addLast(E e) {
            Node<E> n = new Node<>(last, e, null);
            if (last == null) first = n; else last.next = n;
            last = n; size++;
        }
        void addFirst(E e) {
            Node<E> n = new Node<>(null, e, first);
            if (first == null) last = n; else first.prev = n;
            first = n; size++;
        }
        private Node<E> node(int index) { // walk from the nearer end
            Objects.checkIndex(index, size);
            Node<E> x;
            if (index < (size >> 1)) { x = first; for (int i = 0; i < index; i++) x = x.next; }
            else { x = last; for (int i = size - 1; i > index; i--) x = x.prev; }
            return x;
        }
        E get(int i) { return node(i).item; }
        void add(int index, E e) {
            if (index == size) { addLast(e); return; }
            Node<E> succ = node(index);
            Node<E> n = new Node<>(succ.prev, e, succ);
            if (succ.prev == null) first = n; else succ.prev.next = n;
            succ.prev = n; size++;
        }
        E remove(int index) {
            Node<E> x = node(index);
            if (x.prev == null) first = x.next; else x.prev.next = x.next;
            if (x.next == null) last = x.prev; else x.next.prev = x.prev;
            E item = x.item;
            x.item = null; x.prev = x.next = null; // help GC
            size--;
            return item;
        }
        int size() { return size; }
        long sum() { long s = 0; for (Node<E> x = first; x != null; x = x.next) s += (Integer) x.item; return s; }
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) {
        // ---- growth policy: 10 -> 15 -> 22 -> 33 -> 49 -> 73
        SimpleArrayList<Integer> grow = new SimpleArrayList<>();
        for (int i = 0; i < 70; i++) grow.add(i);
        check(grow.growthHistory.subList(0, 5).equals(List.of(15, 22, 33, 49, 73)), "growth " + grow.growthHistory);
        check(grow.capacity() == 73 && grow.size() == 70, "capacity/size");

        // ---- differential test against java.util
        Random rnd = new Random(7);
        List<Integer> jdk = new ArrayList<>();
        SimpleArrayList<Integer> mine = new SimpleArrayList<>();
        SimpleLinkedList<Integer> linked = new SimpleLinkedList<>();
        for (int step = 0; step < 20_000; step++) {
            int op = rnd.nextInt(10);
            if (op < 6 || jdk.isEmpty()) {
                int idx = rnd.nextInt(jdk.size() + 1), v = rnd.nextInt();
                jdk.add(idx, v); mine.add(idx, v); linked.add(idx, v);
            } else {
                int idx = rnd.nextInt(jdk.size());
                int a = jdk.remove(idx), b = mine.remove(idx), c = linked.remove(idx);
                check(a == b && a == c, "removed value mismatch");
            }
        }
        check(jdk.size() == mine.size() && jdk.size() == linked.size(), "sizes");
        for (int i = 0; i < jdk.size(); i++) {
            check(jdk.get(i).equals(mine.get(i)), "array list element " + i);
            check(jdk.get(i).equals(linked.get(i)), "linked list element " + i);
        }

        // ---- fail-fast iterators (ours and the JDK's)
        SimpleArrayList<Integer> ff = new SimpleArrayList<>();
        ff.add(1); ff.add(2); ff.add(3);
        try { for (int x : ff) ff.add(x); check(false, "CME expected"); } catch (ConcurrentModificationException ok) { }
        List<Integer> jl = new ArrayList<>(List.of(1, 2, 3));
        try { for (int x : jl) jl.remove((Integer) x); check(false, "CME expected"); } catch (ConcurrentModificationException ok) { }
        jl.removeIf(x -> x > 1); // correct way
        check(jl.size() <= 1, "removeIf");

        // remove(int) vs remove(Object)
        List<Integer> amb = new ArrayList<>(List.of(10, 20, 1));
        amb.remove(1);
        check(amb.equals(List.of(10, 1)), "remove(int index)");
        amb.remove(Integer.valueOf(1));
        check(amb.equals(List.of(10)), "remove(Object)");

        // ---- benchmarks
        System.out.println("Benchmarks (ms):                      ArrayList  LinkedList  | ours: Array  Linked");
        final int N = 50_000;
        List<Integer> al = new ArrayList<>(), ll = new LinkedList<>();
        for (int i = 0; i < N; i++) { al.add(i); ll.add(i); }
        SimpleArrayList<Integer> sal = new SimpleArrayList<>();
        SimpleLinkedList<Integer> sll = new SimpleLinkedList<>();
        for (int i = 0; i < N; i++) { sal.add(i); sll.addLast(i); }
        Random r = new Random(1);
        int[] idx = r.ints(5_000, 0, N).toArray();

        long sink = 0;
        // random access
        long t0 = System.nanoTime(); for (int i : idx) sink += al.get(i); long tA = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i : idx) sink += ll.get(i); long tL = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i : idx) sink += sal.get(i); long tSA = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i : idx) sink += sll.get(i); long tSL = System.nanoTime() - t0;
        row("random get x5000", tA, tL, tSA, tSL);
        // insert at head
        List<Integer> al2 = new ArrayList<>(al), ll2 = new LinkedList<>(ll);
        t0 = System.nanoTime(); for (int i = 0; i < 20_000; i++) al2.add(0, i); tA = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i = 0; i < 20_000; i++) ll2.add(0, i); tL = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i = 0; i < 20_000; i++) sal.add(0, i); tSA = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i = 0; i < 20_000; i++) sll.add(0, i); tSL = System.nanoTime() - t0;
        row("insert at head x20000", tA, tL, tSA, tSL);
        // insert in the middle
        t0 = System.nanoTime(); for (int i = 0; i < 2_000; i++) al2.add(al2.size() / 2, i); tA = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i = 0; i < 2_000; i++) ll2.add(ll2.size() / 2, i); tL = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i = 0; i < 2_000; i++) sal.add(sal.size() / 2, i); tSA = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int i = 0; i < 2_000; i++) sll.add(sll.size() / 2, i); tSL = System.nanoTime() - t0;
        row("insert in middle x2000", tA, tL, tSA, tSL);
        // append + iterate 1M
        t0 = System.nanoTime(); List<Integer> a3 = new ArrayList<>(); for (int i = 0; i < 1_000_000; i++) a3.add(i); tA = System.nanoTime() - t0;
        t0 = System.nanoTime(); List<Integer> l3 = new LinkedList<>(); for (int i = 0; i < 1_000_000; i++) l3.add(i); tL = System.nanoTime() - t0;
        t0 = System.nanoTime(); SimpleArrayList<Integer> s3 = new SimpleArrayList<>(); for (int i = 0; i < 1_000_000; i++) s3.add(i); tSA = System.nanoTime() - t0;
        t0 = System.nanoTime(); SimpleLinkedList<Integer> sl3 = new SimpleLinkedList<>(); for (int i = 0; i < 1_000_000; i++) sl3.addLast(i); tSL = System.nanoTime() - t0;
        row("append 1M", tA, tL, tSA, tSL);
        t0 = System.nanoTime(); for (int x : a3) sink += x; tA = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int x : l3) sink += x; tL = System.nanoTime() - t0;
        t0 = System.nanoTime(); for (int x : s3) sink += x; tSA = System.nanoTime() - t0;
        t0 = System.nanoTime(); sink += sl3.sum(); tSL = System.nanoTime() - t0;
        row("iterate 1M", tA, tL, tSA, tSL);
        System.out.println("(sink=" + sink + ")  Takeaway: ArrayList wins nearly everywhere; LinkedList only wins pure head-insertion.");
        System.out.println("Day 07 OK");
    }

    static void row(String label, long... nanos) {
        System.out.printf("  %-30s %9.1f %11.1f  | %11.1f %7.1f%n", label, nanos[0] / 1e6, nanos[1] / 1e6, nanos[2] / 1e6, nanos[3] / 1e6);
    }
}
