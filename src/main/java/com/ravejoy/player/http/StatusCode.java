package com.ravejoy.player.http;

public final class StatusCode {
  private StatusCode() {}

  public static final int OK = 200;
  public static final int CREATED = 201;
  public static final int NO_CONTENT = 204;

  public static final int BAD_REQUEST = 400;
  public static final int UNAUTHORIZED = 401;
  public static final int FORBIDDEN = 403;
  public static final int NOT_FOUND = 404;
  public static final int CONFLICT = 409;
  public static final int REQUEST_TIMEOUT = 408;
  public static final int TOO_MANY_REQUESTS = 429;

  public static final int INTERNAL_SERVER_ERROR = 500;
  public static final int BAD_GATEWAY = 502;
  public static final int SERVICE_UNAVAILABLE = 503;
  public static final int GATEWAY_TIMEOUT = 504;
}
