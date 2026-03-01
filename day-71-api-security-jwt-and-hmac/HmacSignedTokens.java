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
 * Day 71 - API Security: HMAC and JWT (short mini-lab: a few lines of code + assertions).
 * Run: javac HmacSignedTokens.java && java HmacSignedTokens
 */
public class HmacSignedTokens {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    static String b64(byte[] b) { return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
    static String hmac(String key, String data) throws Exception {
        Mac m = Mac.getInstance("HmacSHA256"); m.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return b64(m.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
String body = b64("{\"sub\":\"u1\"}".getBytes(StandardCharsets.UTF_8)); String sig = hmac("secret", body);
check(sig.equals(hmac("secret", body)) && !sig.equals(hmac("other", body)), "deterministic and key-dependent");
check(java.security.MessageDigest.isEqual(sig.getBytes(StandardCharsets.UTF_8), hmac("secret", body).getBytes(StandardCharsets.UTF_8)), "constant-time compare");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 71 OK");
    }
}
