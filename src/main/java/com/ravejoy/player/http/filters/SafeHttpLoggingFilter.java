package com.ravejoy.player.http.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SafeHttpLoggingFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(SafeHttpLoggingFilter.class);

  private static final String MASK = "****";
  private static final int MAX_BODY_LOG = 20_000;

  private static final Set<String> SENSITIVE_HEADERS =
      Set.of(
          "authorization",
          "cookie",
          "set-cookie",
          "proxy-authorization",
          "x-api-key",
          "x-auth-token");

  private static final String[] SENSITIVE_FIELDS = {"password", "token", "secret", "key", "apikey"};

  private static final List<Pattern> SENSITIVE_JSON_PATTERNS =
      Arrays.stream(SENSITIVE_FIELDS)
          .map(k -> Pattern.compile("(?i)\"" + Pattern.quote(k) + "\"\\s*:\\s*\"[^\"]*\""))
          .toList();

  private final boolean enabled;

  public SafeHttpLoggingFilter() {
    this(true);
  }

  public SafeHttpLoggingFilter(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public Response filter(
      FilterableRequestSpecification requestSpec,
      FilterableResponseSpecification responseSpec,
      FilterContext ctx) {

    if (enabled && LOG.isDebugEnabled()) {
      LOG.debug(buildMaskedRequestLog(requestSpec));
    }

    Response response = ctx.next(requestSpec, responseSpec);

    if (enabled && LOG.isDebugEnabled()) {
      LOG.debug(buildMaskedResponseLog(response));
    }

    return response;
  }

  private static String buildMaskedRequestLog(FilterableRequestSpecification req) {
    StringBuilder sb = new StringBuilder();
    String uri = maskQuery(req.getURI());
    sb.append(req.getMethod()).append(" ").append(uri).append("\n");
    for (Header h : req.getHeaders().asList()) {
      String name = h.getName();
      String value = isSensitiveHeader(name) ? MASK : h.getValue();
      sb.append(name).append(": ").append(value).append("\n");
    }
    Object body = req.getBody();
    if (body != null) {
      String masked = maskBody(body.toString());
      if (masked.length() > MAX_BODY_LOG)
        masked = masked.substring(0, MAX_BODY_LOG) + "...[truncated]";
      sb.append("\n").append(masked);
    }
    return sb.toString();
  }

  private static String buildMaskedResponseLog(Response resp) {
    StringBuilder sb = new StringBuilder();
    sb.append("HTTP ").append(resp.statusCode()).append("\n");
    resp.getHeaders()
        .asList()
        .forEach(
            h -> {
              String name = h.getName();
              String value = isSensitiveHeader(name) ? MASK : h.getValue();
              sb.append(name).append(": ").append(value).append("\n");
            });
    String body = (resp.getBody() != null) ? resp.getBody().asString() : null;
    if (body != null && !body.isBlank()) {
      String masked = maskBody(body);
      if (masked.length() > MAX_BODY_LOG) {
        masked = masked.substring(0, MAX_BODY_LOG) + "...[truncated]";
      }
      sb.append("\n").append(masked);
    }
    return sb.toString();
  }

  private static boolean isSensitiveHeader(String name) {
    return name != null && SENSITIVE_HEADERS.contains(name.toLowerCase(Locale.ROOT));
  }

  private static String maskBody(String s) {
    String out = s;
    for (int i = 0; i < SENSITIVE_FIELDS.length; i++) {
      String key = SENSITIVE_FIELDS[i];
      out =
          SENSITIVE_JSON_PATTERNS
              .get(i)
              .matcher(out)
              .replaceAll("\"" + key + "\":\"" + MASK + "\"");
    }
    return out;
  }

  private static String maskQuery(String uri) {
    if (uri == null) return null;
    String out = uri;
    for (String key : SENSITIVE_FIELDS) {
      out =
          out.replaceAll("(?i)([?&])" + Pattern.quote(key) + "=([^&#]*)", "$1" + key + "=" + MASK);
    }
    return out;
  }
}
