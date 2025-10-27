package com.ravejoy.player.infra.http;

import com.ravejoy.player.http.StatusCode;
import com.ravejoy.player.http.filters.SafeHttpLoggingFilter;
import io.restassured.RestAssured;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
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

  @Test(description = "Ensures SafeHttpLoggingFilter masks only logs and never alters on-wire headers/body")
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
}
