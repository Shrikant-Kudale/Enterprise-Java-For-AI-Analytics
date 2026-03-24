import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Day 24 - protobuf-style varints + framed TCP RPC with virtual-thread server. Run: javac BinaryRpc.java && java BinaryRpc */
public class BinaryRpc {
    static void putVarint(ByteArrayOutputStream o, long v) {
        while ((v & ~0x7FL) != 0) { o.write((int) ((v & 0x7F) | 0x80)); v >>>= 7; }
        o.write((int) v);
    }
    static long getVarint(ByteBuffer b) {
        long r = 0; int shift = 0;
        while (true) { byte x = b.get(); r |= (long) (x & 0x7F) << shift; if ((x & 0x80) == 0) return r; shift += 7; }
    }
    record Req(String model, long id, float[] x) { }

    static byte[] encodeRequest(String model, long id, float[] x) {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte[] m = model.getBytes(StandardCharsets.UTF_8);
        putVarint(o, (1 << 3) | 2); putVarint(o, m.length); o.write(m, 0, m.length);   // field 1: string
        putVarint(o, 2 << 3); putVarint(o, id);                                         // field 2: varint
        ByteBuffer fb = ByteBuffer.allocate(x.length * 4).order(ByteOrder.LITTLE_ENDIAN);
        for (float f : x) fb.putFloat(f);
        putVarint(o, (3 << 3) | 2); putVarint(o, fb.capacity()); o.write(fb.array(), 0, fb.capacity()); // field 3: packed floats
        return o.toByteArray();
    }
    static Req decodeRequest(byte[] bytes) {
        ByteBuffer b = ByteBuffer.wrap(bytes);
        String model = ""; long id = 0; float[] x = new float[0];
        while (b.hasRemaining()) {
            int tag = (int) getVarint(b), field = tag >>> 3, wire = tag & 7;
            if (wire == 0) { long v = getVarint(b); if (field == 2) id = v; }
            else if (wire == 2) {
                int len = (int) getVarint(b); byte[] d = new byte[len]; b.get(d);
                if (field == 1) model = new String(d, StandardCharsets.UTF_8);
                else if (field == 3) { ByteBuffer fb = ByteBuffer.wrap(d).order(ByteOrder.LITTLE_ENDIAN); x = new float[len / 4]; for (int i = 0; i < x.length; i++) x[i] = fb.getFloat(); }
            } else throw new IllegalArgumentException("unsupported wire type " + wire);   // unknown LEN/varint fields are skipped above
        }
        return new Req(model, id, x);
    }

    static ServerSocket serve() throws IOException {
        ServerSocket ss = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
        Thread.ofPlatform().daemon().start(() -> {
            while (!ss.isClosed()) {
                try { Socket s = ss.accept(); Thread.ofVirtual().start(() -> handle(s)); } catch (IOException e) { return; }
            }
        });
        return ss;
    }
    static void handle(Socket s) {
        try (s; DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()))) {
            s.setTcpNoDelay(true);
            while (true) {
                int len;
                try { len = in.readInt(); } catch (EOFException e) { return; }
                byte[] payload = new byte[len]; in.readFully(payload);
                Req r = decodeRequest(payload);
                float[] y = r.x().clone(); for (int i = 0; i < y.length; i++) y[i] *= 2;   // the "model"
                byte[] resp = encodeRequest("ok", r.id(), y);
                out.writeInt(resp.length); out.write(resp); out.flush();
            }
        } catch (IOException ignored) { }
    }

    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        for (long v : new long[]{0, 1, 127, 128, 300, Integer.MAX_VALUE, Long.MAX_VALUE}) {
            ByteArrayOutputStream o = new ByteArrayOutputStream(); putVarint(o, v);
            check(getVarint(ByteBuffer.wrap(o.toByteArray())) == v, "varint round trip " + v);
        }
        ByteArrayOutputStream o300 = new ByteArrayOutputStream(); putVarint(o300, 300);
        check(o300.size() == 2, "300 fits in 2 bytes");

        float[] x = new float[64]; for (int i = 0; i < x.length; i++) x[i] = i * 0.5f;
        byte[] proto = encodeRequest("resnet", 7, x);
        Req back = decodeRequest(proto);
        check(back.model().equals("resnet") && back.id() == 7 && Arrays.equals(back.x(), x), "message round trip");
        StringBuilder json = new StringBuilder("{\"model\":\"resnet\",\"id\":7,\"x\":[");
        for (int i = 0; i < x.length; i++) json.append(i > 0 ? "," : "").append(x[i]);
        json.append("]}");
        System.out.printf("payload size: protobuf-style %d B vs JSON %d B%n", proto.length, json.length());
        check(proto.length < json.length(), "binary is smaller");

        ServerSocket server = serve();
        try (Socket c = new Socket(InetAddress.getLoopbackAddress(), server.getLocalPort())) {
            c.setTcpNoDelay(true);
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(c.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(c.getInputStream()));
            int n = 20_000; long[] lat = new long[n];
            for (int i = 0; i < n; i++) {
                long t0 = System.nanoTime();
                out.writeInt(proto.length); out.write(proto); out.flush();
                byte[] resp = new byte[in.readInt()]; in.readFully(resp);
                lat[i] = System.nanoTime() - t0;
                if (i == 0) { Req r = decodeRequest(resp); check(r.x()[2] == 2.0f, "server doubled the input"); }
            }
            Arrays.sort(lat);
            System.out.printf("RPC loopback x%d: p50=%.0f us  p99=%.0f us  max=%.0f us%n", n, lat[n / 2] / 1e3, lat[(int) (n * 0.99)] / 1e3, lat[n - 1] / 1e3);
            check(lat[n / 2] < 20_000_000L, "p50 under 20 ms");
            // pipelining: send 500 requests, then read 500 responses in order
            for (int i = 0; i < 500; i++) { byte[] p = encodeRequest("m", i, new float[]{i}); out.writeInt(p.length); out.write(p); }
            out.flush();
            for (int i = 0; i < 500; i++) { byte[] resp = new byte[in.readInt()]; in.readFully(resp); check(decodeRequest(resp).id() == i, "ordered response " + i); }
        }
        server.close();
        System.out.println("Day 24 OK");
    }
}
