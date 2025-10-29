package com.ravejoy.player.http.filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
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
    String url = req.getURI();
    String requestLine = method + " " + url;
    String editor = req.getHeaders() != null ? req.getHeaders().getValue("X-Editor") : null;

    String reqBody = bodyToString(req.getBody());
    String maskedReqBody = maskSecrets(reqBody);

    log.info("[HTTP] → {} | editor={} corrId={}", requestLine, dash(editor), corrId);

    if (log.isDebugEnabled()) {
      String headers =
          req.getHeaders() == null
              ? "-"
              : req.getHeaders().asList().stream()
                  .map(h -> h.getName() + ": " + h.getValue())
                  .collect(Collectors.joining(", "));
      log.debug("[HTTP] → headers: {}", headers);
      if (maskedReqBody != null && !maskedReqBody.isBlank()) {
        String body = pretty ? prettyJsonOrRaw(maskedReqBody) : maskedReqBody;
        log.debug("[HTTP] → body: {}", trim(body, maxBody));
      }
      log.debug("[HTTP] → curl: {}", toCurl(req, maskedReqBody));
    }

    Instant start = Instant.now();
    Response response = ctx.next(req, res);
    long ms = Duration.between(start, Instant.now()).toMillis();

    String contentType = response.getContentType();
    log.info(
        "[HTTP] ← {} {}ms | status={} contentType={} corrId={}",
        requestLine,
        ms,
        response.getStatusCode(),
        dash(contentType),
        corrId);

    if (log.isDebugEnabled()) {
      String respHeaders =
          response.getHeaders() == null
              ? "-"
              : response.getHeaders().asList().stream()
                  .map(h -> h.getName() + ": " + h.getValue())
                  .collect(Collectors.joining(", "));
      log.debug("[HTTP] ← headers: {}", respHeaders);

      String resp = safeBody(response);
      String prettyResp = pretty ? prettyJsonOrRaw(resp) : resp;
      log.debug("[HTTP] ← body: {}", trim(prettyResp, maxBody));
    }

    return response;
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
    if (s == null) return null;
    if (s.length() <= max) return s;
    return s.substring(0, max) + "...(truncated," + s.length() + "B)";
  }

  private static String dash(String s) {
    return s == null || s.isBlank() ? "-" : s;
  }

  private static String maskSecrets(String body) {
    if (body == null) return null;
    String masked = body.replaceAll("(?i)\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"");
    masked = masked.replaceAll("(?i)\"token\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"***\"");
    return masked;
  }

  private static String toCurl(FilterableRequestSpecification req, String maskedBody) {
    StringBuilder b =
        new StringBuilder("curl -X ")
            .append(req.getMethod())
            .append(" '")
            .append(req.getURI())
            .append("'");
    if (req.getHeaders() != null) {
      req.getHeaders()
          .asList()
          .forEach(
              h ->
                  b.append(" -H '")
                      .append(h.getName())
                      .append(": ")
                      .append(h.getValue())
                      .append("'"));
    }
    if (maskedBody != null && !maskedBody.isBlank()) {
      b.append(" --data-raw '").append(maskedBody.replace("'", "'\"'\"'")).append("'");
    }
    return b.toString();
  }
}
