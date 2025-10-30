package com.ravejoy.player.testsupport;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public final class RunIds {
  private static final String SUITE =
      Long.toString(System.currentTimeMillis(), 36).toLowerCase(Locale.ROOT);
  private static final AtomicInteger SEQ = new AtomicInteger(1);
  private static final ThreadLocal<String> TL = ThreadLocal.withInitial(() -> rnd(3));

  private RunIds() {}

  public static String runId() {
    return "at_" + SUITE + "_" + TL.get() + "_" + pad(SEQ.getAndIncrement(), 5);
  }

  public static String prefix() {
    return runId() + "_";
  }

  public static String login(String base) {
    return prefix() + base;
  }

  public static String screen(String base) {
    return prefix() + base;
  }

  private static String rnd(int n) {
    var r = new byte[n];
    ThreadLocalRandom.current().nextBytes(r);
    char[] alphabet = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
    StringBuilder sb = new StringBuilder(n);
    for (byte b : r) sb.append(alphabet[(b & 0xFF) % alphabet.length]);
    return sb.toString();
  }

  private static String pad(int v, int width) {
    String s = Integer.toString(v, 36);
    if (s.length() >= width) return s;
    StringBuilder sb = new StringBuilder(width);
    for (int i = s.length(); i < width; i++) sb.append('0');
    return sb.append(s).toString();
  }
}
