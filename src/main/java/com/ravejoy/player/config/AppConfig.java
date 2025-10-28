package com.ravejoy.player.config;

public final class AppConfig {

  private static String resolveConfigFile() {
    String env = System.getProperty("env", System.getenv("ENV"));
    if (env == null || env.isBlank()) return "application-qa.properties";
    return "application-" + env.toLowerCase() + ".properties";
  }

  private static volatile ConfigLoader L =
      new ConfigLoader(System.getProperty("config.file", resolveConfigFile()));

  public static void reload() {
    L = new ConfigLoader(System.getProperty("config.file", resolveConfigFile()));
  }

  private AppConfig() {}

  public static String apiUrl() {
    return L.string("api.url", true);
  }

  public static int threads() {
    int t = L.integer("threads", 3);
    return t <= 0 ? 3 : t;
  }
}
