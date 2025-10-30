// src/testFixtures/java/com/ravejoy/player/testsupport/helper/Jsons.java
package com.ravejoy.player.testsupport.helper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

public final class Jsons {
  private static final ObjectMapper MAPPER =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private Jsons() {}

  public static <T> T toDto(Response resp, Class<T> type) {
    try {
      return MAPPER.readValue(resp.asString(), type);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to map response to " + type.getSimpleName(), e);
    }
  }

  public static <T> T toDtoOrNull(Response resp, Class<T> type) {
    if (isEmptyBody(resp)) return null;
    return toDto(resp, type);
  }

  public static boolean isEmptyBody(Response resp) {
    var body = resp.getBody().asString();
    return body == null || body.isBlank() || body.equals("{}") || body.equals("[]");
  }

  public static <T> T from(Response resp, Class<T> cls) {
    String body = resp.getBody() != null ? resp.getBody().asString() : null;
    if (body == null || body.isBlank()) {
      throw new IllegalStateException("Cannot deserialize empty body to " + cls.getSimpleName());
    }
    try {
      return MAPPER.readValue(body, cls);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to deserialize response to " + cls.getSimpleName() + ": " + body, e);
    }
  }
}
