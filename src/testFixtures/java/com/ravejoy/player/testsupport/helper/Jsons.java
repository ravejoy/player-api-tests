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
}
