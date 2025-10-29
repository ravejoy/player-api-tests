package com.ravejoy.player.infra.http;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ravejoy.player.http.StatusCode;
import com.ravejoy.player.http.filters.SafeHttpLoggingFilter;
import io.restassured.RestAssured;
import java.io.IOException;
import java.util.stream.Collectors;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = {"infra"})
public class SafeHttpLoggingFilterTest {

  private MockWebServer server;

  @BeforeMethod
  void start() throws IOException {
    server = new MockWebServer();
    server.start();
  }

  @AfterMethod
  void stop() throws IOException {
    server.shutdown();
  }

  @Test(
      description =
          "Ensures SafeHttpLoggingFilter masks only logs and never alters on-wire headers/body")
  public void shouldNotMutateWireData() throws InterruptedException {
    server.enqueue(new MockResponse().setResponseCode(StatusCode.OK));

    String body = "{\"username\":\"john\",\"password\":\"secret\",\"token\":\"abc123\"}";

    var response =
        RestAssured.given()
            .baseUri(server.url("/").toString())
            .header("Authorization", "Bearer real_token")
            .header("Cookie", "session=xyz")
            .body(body)
            .filter(new SafeHttpLoggingFilter(true))
            .when()
            .post("/")
            .then()
            .extract()
            .response();

    var recorded = server.takeRequest();

    Assert.assertEquals(recorded.getHeader("Authorization"), "Bearer real_token");
    Assert.assertEquals(recorded.getHeader("Cookie"), "session=xyz");
    Assert.assertTrue(recorded.getBody().readUtf8().contains("secret"));
    Assert.assertEquals(response.statusCode(), StatusCode.OK);
  }

  @Test(
      description =
          "Masks sensitive fields in response body logging while preserving raw on-wire response")
  public void shouldMaskSensitiveFieldsInResponseBodyLog() {
    String responseJson = "{\"password\":\"abc123\",\"token\":\"t1\",\"username\":\"john\"}";
    server.enqueue(
        new MockResponse()
            .setResponseCode(StatusCode.OK)
            .addHeader("Content-Type", "application/json")
            .setBody(responseJson));

    // ---- enable masking just for this test (CI-proof & local-proof)
    String prevMask = System.getProperty("http.mask");
    System.setProperty("http.mask", "true");
    try {
      Logger logger = (Logger) LoggerFactory.getLogger(SafeHttpLoggingFilter.class);
      Level prev = logger.getLevel();
      ListAppender<ILoggingEvent> appender = new ListAppender<>();
      appender.start();
      logger.setLevel(Level.DEBUG);
      logger.addAppender(appender);

      var response =
          RestAssured.given()
              .baseUri(server.url("/").toString())
              .filter(new SafeHttpLoggingFilter(true))
              .when()
              .get("/")
              .then()
              .extract()
              .response();

      String logs =
          appender.list.stream()
              .map(ILoggingEvent::getFormattedMessage)
              .collect(Collectors.joining("\n"));

      logger.detachAppender(appender);
      logger.setLevel(prev);

      Assert.assertEquals(response.statusCode(), StatusCode.OK);
      Assert.assertTrue(response.asString().contains("\"password\":\"abc123\""));
      Assert.assertTrue(logs.contains("\"password\":\"****\""));
      Assert.assertFalse(logs.contains("\"password\":\"abc123\""));
    } finally {
      if (prevMask != null) System.setProperty("http.mask", prevMask);
      else System.clearProperty("http.mask");
    }
  }
}
