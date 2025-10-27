package com.ravejoy.player.http;

import static io.restassured.RestAssured.given;

import io.restassured.filter.Filter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

public final class ApiClient {
  private final RequestSpecification baseSpec;

  public ApiClient() {
    this(RequestSpecFactory.defaultSpec());
  }

  public ApiClient(RequestSpecification spec) {
    this.baseSpec = spec;
  }

  private RequestSpecification req() {
    return given().spec(baseSpec);
  }

  public ApiClient withFilters(Filter... filters) {
    if (filters == null || filters.length == 0) return this;
    RequestSpecification s = req();
    for (Filter f : filters) s = s.filter(f);
    return new ApiClient(s);
  }

  public ApiClient withHeaders(Map<String, ?> headers) {
    return (headers == null || headers.isEmpty()) ? this : new ApiClient(req().headers(headers));
  }

  public ApiClient withHeader(String name, Object value) {
    return (name == null) ? this : new ApiClient(req().header(name, value));
  }

  public ApiClient withCookies(Map<String, ?> cookies) {
    return (cookies == null || cookies.isEmpty()) ? this : new ApiClient(req().cookies(cookies));
  }

  public ApiClient withQuery(Map<String, ?> query) {
    return (query == null || query.isEmpty()) ? this : new ApiClient(req().queryParams(query));
  }

  public Response get(String path) {
    return req().when().get(path).then().extract().response();
  }

  public Response get(String path, Map<String, ?> query) {
    return (query == null || query.isEmpty())
        ? get(path)
        : req().queryParams(query).when().get(path).then().extract().response();
  }

  public Response get(String path, Object... pathParams) {
    return req().when().get(path, pathParams).then().extract().response();
  }

  public Response post(String path, Object body) {
    return req().body(body).when().post(path).then().extract().response();
  }

  public Response put(String path, Object body) {
    return req().body(body).when().put(path).then().extract().response();
  }

  public Response patch(String path, Object body) {
    return req().body(body).when().patch(path).then().extract().response();
  }

  public Response delete(String path) {
    return req().when().delete(path).then().extract().response();
  }

  public Response delete(String path, Object... pathParams) {
    return req().when().delete(path, pathParams).then().extract().response();
  }

  public Response deleteWithBody(String path, Object body) {
    return req().body(body).when().delete(path).then().extract().response();
  }
}
