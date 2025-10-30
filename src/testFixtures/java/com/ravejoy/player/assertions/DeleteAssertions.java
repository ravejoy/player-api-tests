package com.ravejoy.player.assertions;

import static com.ravejoy.player.http.StatusCode.FORBIDDEN;
import static com.ravejoy.player.http.StatusCode.NO_CONTENT;
import static com.ravejoy.player.http.StatusCode.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.ravejoy.player.testsupport.helper.Jsons;
import io.restassured.response.Response;

public final class DeleteAssertions {

  public static void assertDeleted204(Response resp) {
    assertEquals(resp.statusCode(), NO_CONTENT, "DELETE must return 204");
    ResponseAsserts.assertEmptyBody(resp);
  }

  public static void assertGetByIdReturnsEmptyBody200(Response resp) {
    assertEquals(resp.statusCode(), OK, "GET after delete => 200 empty body");
    assertTrue(Jsons.isEmptyBody(resp), "Expected empty body for deleted entity");
  }

  public static void assertForbidden(Response resp) {
    ResponseAsserts.assertStatus(resp, FORBIDDEN);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }

  public static void assertDeletedOkOr204Empty(Response resp) {
  ResponseAsserts.assertOkOrNoContentJsonOrEmpty(resp);

  if (resp.statusCode() == com.ravejoy.player.http.StatusCode.NO_CONTENT) {
    ResponseAsserts.assertEmptyBody(resp);
  } else {
    assertTrue(Jsons.isEmptyBody(resp), "Expected empty body for DELETE 200");
  }
}

}

