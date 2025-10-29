package com.ravejoy.player.http.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public final class SafeHttpLoggingFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(SafeHttpLoggingFilter.class);
  private static final ObjectMapper OM =
      new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  private final boolean pretty;
  private final int maxBody;

  public SafeHttpLoggingFilter() {
    this.pretty = Boolean.parseBoolean(System.getProperty("http.log.pretty", "false"));
    this.maxBody = Integer.parseInt(System.getProperty("http.log.maxBody", "4000"));
  }

  public SafeHttpLoggingFilter(boolean ignoredLegacyArg) {
    this();
  }

  @Override
  public Response filter(
      FilterableRequestSpecification req, FilterableResponseSpecification res, FilterContext ctx) {

    String corrId = MDC.get("corrId");
    if (corrId == null || corrId.isBlank()) {
      corrId = UUID.randomUUID().toString();
      MDC.put("corrId", corrId);
    }

    String method = req.getMethod();
    String uriMasked = maskUri(req.getURI());
    String requestLine = method + " " + uriMasked;
    String editor = req.getHeaders() != null ? req.getHeaders().getValue("X-Editor") : null;

    String reqBody = bodyToString(req.getBody());
    String maskedReqBody = maskJsonSecrets(reqBody);

    log.info("[HTTP] → {} | editor={} corrId={}", requestLine, dash(editor), corrId);

    if (log.isDebugEnabled()) {
      String hdrs =
          req.getHeaders() == null
              ? "-"
              : req.getHeaders().asList().stream()
                  .map(h -> h.getName() + ": " + maskHeader(h.getName(), h.getValue()))
                  .collect(Collectors.joining(", "));
      log.debug("[HTTP] → headers: {}", hdrs);

      if (shouldLogBody(req.getContentType()) && notBlank(maskedReqBody)) {
        String body = pretty ? prettyJsonOrRaw(maskedReqBody) : maskedReqBody;
        log.debug("[HTTP] → body: {}", trim(body, maxBody));
      }

      log.debug("[HTTP] → curl: {}", toCurl(req, maskedReqBody));
    }

    Instant start = Instant.now();
    Response response = ctx.next(req, res);
    long tookMs = Duration.between(start, Instant.now()).toMillis();

    String contentType = response.getContentType();
    log.info(
        "[HTTP] ← {} {}ms | status={} contentType={} corrId={}",
        requestLine,
        tookMs,
        response.getStatusCode(),
        dash(contentType),
        corrId);

    if (log.isDebugEnabled()) {
      String rhdrs =
          response.getHeaders() == null
              ? "-"
              : response.getHeaders().asList().stream()
                  .map(h -> h.getName() + ": " + maskHeader(h.getName(), h.getValue()))
                  .collect(Collectors.joining(", "));
      log.debug("[HTTP] ← headers: {}", rhdrs);

      if (shouldLogBody(contentType)) {
        String resp = safeBody(response);
        String maskedResp = maskJsonSecrets(resp);
        String body = pretty ? prettyJsonOrRaw(maskedResp) : maskedResp;
        log.debug("[HTTP] ← body: {}", trim(body, maxBody));
      } else {
        log.debug("[HTTP] ← body: <body omitted>");
      }
    }

    return response;
  }

  private static boolean shouldLogBody(String contentType) {
    if (contentType == null) return true;
    String ct = contentType.toLowerCase(Locale.ROOT);
    if (ct.startsWith("application/json") || ct.startsWith("text/")) return true;
    if (ct.startsWith("multipart/") || ct.startsWith("application/octet-stream")) return false;
    return false;
  }

  private static String maskHeader(String name, String value) {
    if (value == null) return null;
    String n = name == null ? "" : name.toLowerCase(Locale.ROOT);
    if (n.equals("authorization") || n.equals("cookie") || n.equals("set-cookie")) return "****";
    return value;
  }

  private static String maskUri(String uri) {
    try {
      URI u = new URI(uri);
      String q = u.getRawQuery();
      if (q == null || q.isEmpty()) return uri;

      String[] pairs = q.split("&");
      boolean changed = false;
      for (int i = 0; i < pairs.length; i++) {
        int idx = pairs[i].indexOf('=');
        if (idx > 0) {
          String k = pairs[i].substring(0, idx).toLowerCase(Locale.ROOT);
          if (k.equals("token") || k.equals("password") || k.equals("api_key")) {
            pairs[i] = pairs[i].substring(0, idx + 1) + "****";
            changed = true;
          }
        }
      }
      if (!changed) return uri;

      String newQuery = String.join("&", pairs);
      URI masked =
          new URI(
              u.getScheme(),
              u.getUserInfo(),
              u.getHost(),
              u.getPort(),
              u.getPath(),
              newQuery,
              u.getFragment());
      return masked.toString();
    } catch (URISyntaxException e) {
      return uri;
    }
  }

  private static String maskJsonSecrets(String body) {
    if (body == null) return null;
    String s = body;
    s = s.replaceAll("(?i)\"\\s*password\\s*\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"****\"");
    s = s.replaceAll("(?i)\"\\s*token\\s*\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"****\"");
    return s;
  }

  private static String bodyToString(Object body) {
    if (body == null) return null;
    String s = body.toString();
    return "null".equals(s) ? null : s;
  }

  private static String safeBody(Response r) {
    try {
      return new String(r.asByteArray(), StandardCharsets.UTF_8);
    } catch (Throwable t) {
      return "<unreadable>";
    }
  }

  private static String prettyJsonOrRaw(String raw) {
    try {
      JsonNode n = OM.readTree(raw);
      return OM.writeValueAsString(n);
    } catch (Throwable ignore) {
      return raw;
    }
  }

  private static String trim(String s, int max) {
    if (s == null || s.length() <= max) return s;
    return s.substring(0, max) + "...(truncated," + s.length() + "B)";
  }

  private static String dash(String s) {
    return (s == null || s.isBlank()) ? "-" : s;
  }

  private static boolean notBlank(String s) {
    return s != null && !s.isBlank();
  }

  private static String toCurl(FilterableRequestSpecification req, String maskedBody) {
    StringBuilder b =
        new StringBuilder("curl -X ")
            .append(req.getMethod())
            .append(" '")
            .append(maskUri(req.getURI()))
            .append("'");
    if (req.getHeaders() != null) {
      req.getHeaders()
          .asList()
          .forEach(
              h ->
                  b.append(" -H '")
                      .append(h.getName())
                      .append(": ")
                      .append(maskHeader(h.getName(), h.getValue()))
                      .append("'"));
    }
    if (notBlank(maskedBody)) {
      b.append(" --data-raw '").append(maskedBody.replace("'", "'\"'\"'")).append("'");
    }
    return b.toString();
  }
}
