package com.ravejoy.player.config;

public final class AppConfig {
  private static final String CFG_FILE =
      System.getProperty("config.file", "application.properties");
  private static final ConfigLoader L = new ConfigLoader(CFG_FILE);

  private static final String API_URL = L.string("api.url", true);
  private static final int THREADS_RAW = L.integer("threads", 3);
  private static final int THREADS = (THREADS_RAW <= 0) ? 3 : THREADS_RAW;

  private AppConfig() {}

  public static String apiUrl() { return API_URL; }
  public static int threads() { return THREADS; }
}
