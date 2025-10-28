package com.ravejoy.player.infra.health;

import com.ravejoy.player.config.AppConfig;
import com.ravejoy.player.http.StatusCode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"infra"})
public class HealthCheckTest {

  @Test(description = "Verifies that the Player API is reachable and responds with HTTP 200 OK.")
  public void getAllPlayers_isReachable() {
    Response r =
        RestAssured.given()
            .baseUri(AppConfig.apiUrl())
            .when()
            .get("/player/get/all")
            .then()
            .extract()
            .response();

    Assert.assertEquals(r.statusCode(), StatusCode.OK, "Expected 200 from /player/get/all");
    String ct = r.getContentType();
    Assert.assertTrue(
        ct != null && ct.toLowerCase().startsWith(ContentType.JSON.toString()),
        "Expected application/json content type");
  }
}
