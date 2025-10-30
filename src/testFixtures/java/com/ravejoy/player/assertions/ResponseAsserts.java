package com.ravejoy.player.assertions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.ravejoy.player.http.StatusCode;
import com.ravejoy.player.testsupport.HttpHeader;
import com.ravejoy.player.testsupport.MediaType;
import com.ravejoy.player.testsupport.helper.Jsons;

import io.restassured.response.Response;

public final class ResponseAsserts {
  private ResponseAsserts() {}

  public static void assertOkJson(Response resp) {
    assertEquals(resp.statusCode(), StatusCode.OK, "status");
    assertJson(resp);
  }

  public static void assertStatus(Response resp, int expected) {
    assertEquals(resp.statusCode(), expected, "status");
  }

  public static void assertJson(Response resp) {
    String ct = resp.getHeader(HttpHeader.CONTENT_TYPE);
    assertTrue(
        ct != null && ct.toLowerCase().startsWith(MediaType.APPLICATION_JSON), "content-type");
  }

  public static void assertJsonOrEmpty(Response resp) {
    String ct = resp.getHeader(HttpHeader.CONTENT_TYPE);
    String body = resp.getBody() != null ? resp.getBody().asString() : null;
    boolean empty = body == null || body.isBlank();
    boolean isJson = ct != null && ct.toLowerCase().startsWith(MediaType.APPLICATION_JSON);
    assertTrue(empty || isJson, "content-type-or-empty");
  }

   public static void assertOkOrNoContentJsonOrEmpty(Response resp) {
  int s = resp.statusCode();
  assertTrue(s == StatusCode.OK || s == StatusCode.NO_CONTENT,
      "Expected 200 or 204, got " + s);

  if (s == StatusCode.OK) {
    assertJsonOrEmpty(resp);
  } else {
    assertEmptyBody(resp);
  }
}

public static void assertEmptyBody(Response resp) {
  assertTrue(Jsons.isEmptyBody(resp), "Expected empty body");
}

public static void assertNoContent(Response resp) {
  assertEquals(resp.statusCode(), StatusCode.NO_CONTENT, "status");
  assertEmptyBody(resp);
}

public static void assertStatusIn(Response resp, int... allowed) {
  int s = resp.statusCode();
  boolean ok = false;
  for (int a : allowed) if (s == a) { ok = true; break; }
  assertTrue(ok, "Unexpected status " + s);
}
}
