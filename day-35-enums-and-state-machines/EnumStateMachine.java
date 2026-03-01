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
 * Day 35 - Enums and State Machines (short mini-lab: a few lines of code + assertions).
 * Run: javac EnumStateMachine.java && java EnumStateMachine
 */
public class EnumStateMachine {
    static void check(boolean c, String m) { if (!c) throw new AssertionError(m); }

    enum State { NEW { State next() { return RUNNING; } }, RUNNING { State next() { return DONE; } }, DONE { State next() { return this; } }; abstract State next(); }

    public static void main(String[] args) throws Exception {
        long started = System.nanoTime();
State s = State.NEW; s = s.next().next();
check(s == State.DONE && s.next() == State.DONE, "terminal state is sticky");
EnumMap<State, Integer> m = new EnumMap<>(State.class); m.put(State.NEW, 1);
check(m.size() == 1 && EnumSet.allOf(State.class).size() == 3, "enum collections");
        System.out.printf("elapsed %.2f ms%n", (System.nanoTime() - started) / 1e6);
        System.out.println("Day 35 OK");
    }
}
