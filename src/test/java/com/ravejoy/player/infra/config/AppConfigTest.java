package com.ravejoy.player.infra.config;

import com.ravejoy.player.config.AppConfig;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Test(groups = "infra")
public class AppConfigTest {

  @AfterMethod
  public void tearDown() {
    System.clearProperty("config.file");
    System.clearProperty("api.url");
    System.clearProperty("threads");
    AppConfig.reload();
  }

  @Test(description = "Reads api.url and threads from config file")
  public void readsFromConfigFile() throws IOException {
    Path tmp = Files.createTempFile("appcfg-", ".properties");
    Files.writeString(tmp, "api.url=http://cfg\nthreads=3\n", StandardCharsets.UTF_8);

    System.setProperty("config.file", tmp.toString());
    AppConfig.reload();

    Assert.assertEquals(AppConfig.apiUrl(), "http://cfg");
    Assert.assertEquals(AppConfig.threads(), 3);

    Files.deleteIfExists(tmp);
  }

  @Test(description = "Threads value is clamped to minimum when non-positive")
  public void clampsNonPositiveThreads() throws IOException {
    Path tmp = Files.createTempFile("appcfg-", ".properties");
    Files.writeString(tmp, "api.url=http://x\nthreads=0\n", StandardCharsets.UTF_8);

    System.setProperty("config.file", tmp.toString());
    AppConfig.reload();

    Assert.assertEquals(AppConfig.threads(), 3);

    Files.deleteIfExists(tmp);
  }

  @Test(description = "System properties override file values")
  public void systemPropsOverrideFile() throws IOException {
    Path tmp = Files.createTempFile("appcfg-", ".properties");
    Files.writeString(tmp, "api.url=http://file\nthreads=2\n", StandardCharsets.UTF_8);

    System.setProperty("config.file", tmp.toString());
    System.setProperty("api.url", "http://sys");
    System.setProperty("threads", "7");
    AppConfig.reload();

    Assert.assertEquals(AppConfig.apiUrl(), "http://sys");
    Assert.assertEquals(AppConfig.threads(), 7);

    Files.deleteIfExists(tmp);
  }
}
