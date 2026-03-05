import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Random;

/**
 * Day 05 - Fixed-width binary feature rows written/read four ways, plus a CSV baseline.
 * Record layout (73 bytes): long id | 16 x float | byte label  (big-endian).
 *
 * <p>Run: {@code javac FastDataSerialization.java && java FastDataSerialization}
 */
public class FastDataSerialization {

    static final int N = 200_000, F = 16, REC = 8 + F * 4 + 1;

    record Row(long id, float[] f, byte label) {}

    static Row[] generate() {
        Random r = new Random(42);
        Row[] rows = new Row[N];
        for (int i = 0; i < N; i++) {
            float[] f = new float[F];
            for (int j = 0; j < F; j++) f[j] = r.nextFloat() * 100f;
            rows[i] = new Row(i, f, (byte) (r.nextInt(2)));
        }
        return rows;
    }

    // ------------------------------------------------------------ 1. DataOutputStream (buffered)
    static void writeStream(Path p, Row[] rows) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(p), 1 << 16))) {
            for (Row r : rows) {
                out.writeLong(r.id());
                for (float v : r.f()) out.writeFloat(v);
                out.writeByte(r.label());
            }
        }
    }
    static double readStream(Path p) throws IOException {
        double sum = 0;
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(p), 1 << 16))) {
            for (int i = 0; i < N; i++) {
                sum += in.readLong();
                for (int j = 0; j < F; j++) sum += in.readFloat();
                sum += in.readByte();
            }
        }
        return sum;
    }

    // ------------------------------------------------------------ 2. FileChannel + direct ByteBuffer
    static void writeChannel(Path p, Row[] rows) throws IOException {
        try (FileChannel ch = FileChannel.open(p, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            ByteBuffer buf = ByteBuffer.allocateDirect(REC * 4096);
            for (Row r : rows) {
                if (buf.remaining() < REC) { buf.flip(); while (buf.hasRemaining()) ch.write(buf); buf.clear(); }
                buf.putLong(r.id());
                for (float v : r.f()) buf.putFloat(v);
                buf.put(r.label());
            }
            buf.flip();
            while (buf.hasRemaining()) ch.write(buf);
        }
    }
    static double readChannel(Path p) throws IOException {
        double sum = 0;
        try (FileChannel ch = FileChannel.open(p, StandardOpenOption.READ)) {
            ByteBuffer buf = ByteBuffer.allocateDirect(REC * 4096);
            while (true) {
                int read = ch.read(buf);
                buf.flip();
                while (buf.remaining() >= REC) {
                    sum += buf.getLong();
                    for (int j = 0; j < F; j++) sum += buf.getFloat();
                    sum += buf.get();
                }
                buf.compact();   // keep any partial record for the next fill
                if (read == -1) break;
            }
        }
        return sum;
    }

    // ------------------------------------------------------------ 3. Memory-mapped random access
    static double readMapped(Path p) throws IOException {
        try (FileChannel ch = FileChannel.open(p, StandardOpenOption.READ)) {
            MappedByteBuffer m = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size());
            double sum = 0;
            for (int i = 0; i < N; i++) {
                int off = i * REC;
                sum += m.getLong(off);
                for (int j = 0; j < F; j++) sum += m.getFloat(off + 8 + j * 4);
                sum += m.get(off + 8 + F * 4);
            }
            return sum;
        }
    }
    static float mappedFeature(Path p, int row, int col) throws IOException {
        try (FileChannel ch = FileChannel.open(p, StandardOpenOption.READ)) {
            MappedByteBuffer m = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size());
            return m.getFloat(row * REC + 8 + col * 4); // O(1) random access
        }
    }

    // ------------------------------------------------------------ 4. CSV baseline
    static void writeCsv(Path p, Row[] rows) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            for (Row r : rows) {
                sb.setLength(0);
                sb.append(r.id());
                for (float v : r.f()) sb.append(',').append(v);
                sb.append(',').append(r.label()).append('\n');
                w.write(sb.toString());
            }
        }
    }
    static double readCsv(Path p) throws IOException {
        double sum = 0;
        try (BufferedReader rd = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            while ((line = rd.readLine()) != null) {
                String[] t = line.split(",");
                sum += Long.parseLong(t[0]);
                for (int j = 0; j < F; j++) sum += Float.parseFloat(t[1 + j]);
                sum += Byte.parseByte(t[1 + F]);
            }
        }
        return sum;
    }

    interface IoOp<T> { T run() throws IOException; }
    static <T> T timed(String label, IoOp<T> op) throws IOException {
        long t0 = System.nanoTime();
        T r = op.run();
        System.out.printf("  %-28s %8.1f ms%n", label, (System.nanoTime() - t0) / 1e6);
        return r;
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws IOException {
        Row[] rows = generate();
        double expected = 0;
        for (Row r : rows) { expected += r.id(); for (float v : r.f()) expected += v; expected += r.label(); }

        Path dir = Files.createTempDirectory("day05");
        Path stream = dir.resolve("stream.bin"), channel = dir.resolve("channel.bin"), csv = dir.resolve("rows.csv");
        try {
            System.out.println("Write:");
            timed("DataOutputStream", () -> { writeStream(stream, rows); return null; });
            timed("FileChannel + direct buf", () -> { writeChannel(channel, rows); return null; });
            timed("CSV text", () -> { writeCsv(csv, rows); return null; });

            check(Files.size(stream) == (long) N * REC, "binary size = N * 73");
            check(Files.mismatch(stream, channel) == -1, "stream and channel writers must produce identical bytes");
            check(Files.size(csv) > 2 * Files.size(stream), "CSV must be >2x larger than binary");
            System.out.printf("  sizes: binary=%,d B  csv=%,d B (%.1fx)%n", Files.size(stream), Files.size(csv),
                    Files.size(csv) / (double) Files.size(stream));

            System.out.println("Read (checksum must equal in-memory data):");
            for (int round = 0; round < 2; round++) { // 2nd round shows warm JIT + page cache
                double a = timed("DataInputStream", () -> readStream(stream));
                double b = timed("FileChannel + direct buf", () -> readChannel(channel));
                double c = timed("mmap absolute reads", () -> readMapped(stream));
                double d = timed("CSV parse", () -> readCsv(csv));
                check(a == expected && b == expected && c == expected, "binary checksum mismatch");
                check(d == expected, "CSV float round-trip must be exact with Float.toString");
            }

            Row probe = rows[123_456];
            check(mappedFeature(stream, 123_456, 7) == probe.f()[7], "O(1) mmap random access");
        } finally {
            Files.deleteIfExists(stream); Files.deleteIfExists(channel); Files.deleteIfExists(csv); Files.deleteIfExists(dir);
        }
        System.out.println("Day 05 OK");
    }
}
