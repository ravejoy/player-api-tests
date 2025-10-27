package com.ravejoy.player.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ConfigLoader {
  private final Properties props = new Properties();

  public ConfigLoader(String fileName) {
    if (fileName == null || fileName.isBlank()) return;

    try {
      Path p = Path.of(fileName);
      if (Files.exists(p)) {
        try (var in = Files.newInputStream(p);
             var r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
          props.load(r);
          return;
        }
      }
    } catch (IOException ignored) { }

    try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
      if (in != null) {
        try (var r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
          props.load(r);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load properties: " + fileName, e);
    }
  }

  private static String toEnvKey(String key) {
    return key.replace('.', '_').toUpperCase();
  }

  public String string(String key, boolean required) {
    String value = System.getProperty(key);
    if (value == null) value = System.getenv(toEnvKey(key));
    if (value == null) value = props.getProperty(key);
    if (required && (value == null || value.isBlank()))
      throw new IllegalStateException("Missing required property: " + key);
    return value;
  }

  public int integer(String key, int def) {
    String raw = System.getProperty(key);
    if (raw == null) raw = System.getenv(toEnvKey(key));
    if (raw == null) raw = props.getProperty(key);
    try { return (raw == null) ? def : Integer.parseInt(raw.trim()); }
    catch (NumberFormatException e) { return def; }
  }
}
