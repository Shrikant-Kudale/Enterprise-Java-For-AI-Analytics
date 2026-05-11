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
 * Day 72 - Password Hashing and Secrets (short mini-lab: a few lines of code + assertions).
 * Run: javac Pbkdf2Passwords.java && java Pbkdf2Passwords
 */
public class Pbkdf2Passwords {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256"); byte[] salt = "saltsalt12345678".getBytes(StandardCharsets.UTF_8);
byte[] h1 = f.generateSecret(new PBEKeySpec("pw".toCharArray(), salt, 10_000, 256)).getEncoded();
byte[] h2 = f.generateSecret(new PBEKeySpec("pw".toCharArray(), salt, 10_000, 256)).getEncoded();
byte[] h3 = f.generateSecret(new PBEKeySpec("pw2".toCharArray(), salt, 10_000, 256)).getEncoded();
check(Arrays.equals(h1, h2) && h1.length == 32 && !Arrays.equals(h1, h3), "PBKDF2 deterministic per password+salt");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 72 OK");
    }
}
