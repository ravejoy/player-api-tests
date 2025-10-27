package com.ravejoy.player.http;

import static io.restassured.RestAssured.given;

import com.ravejoy.player.config.AppConfig;
import com.ravejoy.player.http.filters.SafeHttpLoggingFilter;
import io.restassured.config.EncoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

public final class RequestSpecFactory {

  private static final int CONNECT_TIMEOUT_MS = 5_000;
  private static final int SOCKET_TIMEOUT_MS = 10_000;

  private RequestSpecFactory() {}

  public static RequestSpecification defaultSpec(Filter... filters) {
    RequestSpecification spec =
        given()
            .baseUri(AppConfig.apiUrl())
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .config(timeoutAndEncodingConfig());

    if (Boolean.getBoolean("http.mask")) {
      spec.filter(new SafeHttpLoggingFilter(true));
    }

    String logMode = System.getProperty("http.log", "false");
    if ("all".equalsIgnoreCase(logMode)) {
      spec.filter(new RequestLoggingFilter(LogDetail.ALL))
          .filter(new ResponseLoggingFilter(LogDetail.ALL));
    } else if (Boolean.parseBoolean(logMode)) {
      spec.filter(new RequestLoggingFilter(LogDetail.URI))
          .filter(new ResponseLoggingFilter(LogDetail.STATUS));
    }

    if (filters != null) for (Filter f : filters) spec.filter(f);

    return spec;
  }

  private static RestAssuredConfig timeoutAndEncodingConfig() {
    RequestConfig rc =
        RequestConfig.custom()
            .setConnectTimeout(CONNECT_TIMEOUT_MS)
            .setSocketTimeout(SOCKET_TIMEOUT_MS)
            .setConnectionRequestTimeout(CONNECT_TIMEOUT_MS)
            .build();

    return RestAssuredConfig.config()
        .encoderConfig(
            EncoderConfig.encoderConfig()
                .appendDefaultContentCharsetToContentTypeIfUndefined(false))
        .httpClient(
            HttpClientConfig.httpClientConfig()
                .httpClientFactory(() -> HttpClientBuilder.create().setDefaultRequestConfig(rc).build()));
  }
}
