package com.ravejoy.player.testsupport;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public final class RunIds {
  private static final String RUN_ID = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

  private RunIds() {}

  public static String runId() {
    return RUN_ID;
  }

  public static String prefix() {
    return "autotest_" + RUN_ID + "_";
  }

  public static String login(String base) {
    return prefix() + base;
  }

  public static String screen(String base) {
    return prefix() + base;
  }
}
