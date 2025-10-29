package com.ravejoy.player.assertions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.ravejoy.player.http.StatusCode;
import com.ravejoy.player.testsupport.HttpHeader;
import com.ravejoy.player.testsupport.MediaType;
import io.restassured.response.Response;

public final class ResponseAsserts {
  private ResponseAsserts() {}

  public static void assertOkJson(Response resp) {
    assertEquals(resp.statusCode(), StatusCode.OK, "Unexpected status code");
    String ct = resp.getHeader(HttpHeader.CONTENT_TYPE);
    assertTrue(
        ct != null && ct.toLowerCase().startsWith(MediaType.APPLICATION_JSON),
        "Content-Type should be application/json");
  }
}
