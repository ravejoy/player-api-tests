package com.ravejoy.player.config;

public final class AppConfig {
  private static volatile ConfigLoader L =
      new ConfigLoader(System.getProperty("config.file", "application.properties"));

  public static void reload() {
    L = new ConfigLoader(System.getProperty("config.file", "application.properties"));
  }

  private AppConfig() {}

  public static String apiUrl() {
    return L.string("api.url", true);
  }

  public static int threads() {
    int raw = L.integer("threads", 3);
    return (raw <= 0) ? 3 : raw;
  }
}
