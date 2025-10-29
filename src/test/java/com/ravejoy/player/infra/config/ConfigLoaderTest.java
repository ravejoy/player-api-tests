package com.ravejoy.player.infra.config;

import com.ravejoy.player.config.ConfigLoader;
import com.ravejoy.player.testsupport.Groups;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Test(groups = Groups.INFRA)
public class ConfigLoaderTest {

  @AfterMethod
  public void clearSysProps() {
    System.clearProperty("api.url");
    System.clearProperty("threads");
  }

  @Test(description = "Returns null for optional string and uses default for integer when missing")
  public void defaultsWhenNothingProvided() {
    ConfigLoader l = new ConfigLoader(null); // нічого не вантажимо
    Assert.assertNull(l.string("api.url", false)); // optional → null
    Assert.assertEquals(l.integer("threads", 3), 3); // integer має дефолт
  }

  @Test(description = "Loads values from properties file (FS path)")
  public void loadsFromFile() throws Exception {
    Path tmp = Files.createTempFile("cfg-", ".properties");
    Files.writeString(tmp, "api.url=http://from-file\nthreads=5\n", StandardCharsets.UTF_8);

    try {
      ConfigLoader l = new ConfigLoader(tmp.toString());
      Assert.assertEquals(l.string("api.url", true), "http://from-file");
      Assert.assertEquals(l.integer("threads", 1), 5);
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test(description = "System properties override file values")
  public void systemPropsOverride() throws Exception {
    Path tmp = Files.createTempFile("cfg-", ".properties");
    Files.writeString(tmp, "api.url=http://file\nthreads=2\n", StandardCharsets.UTF_8);

    try {
      System.setProperty("api.url", "http://sys");
      System.setProperty("threads", "7");

      ConfigLoader l = new ConfigLoader(tmp.toString());
      Assert.assertEquals(l.string("api.url", true), "http://sys");
      Assert.assertEquals(l.integer("threads", 1), 7);
    } finally {
      Files.deleteIfExists(tmp);
    }
  }

  @Test(description = "Integer parsing is safe; non-numeric falls back to default")
  public void integerSafeParse() throws Exception {
    Path tmp = Files.createTempFile("cfg-", ".properties");
    Files.writeString(tmp, "threads=not-a-number\n", StandardCharsets.UTF_8);

    try {
      ConfigLoader l = new ConfigLoader(tmp.toString());
      Assert.assertEquals(l.integer("threads", 3), 3); // дефолт
    } finally {
      Files.deleteIfExists(tmp);
    }
  }
}
