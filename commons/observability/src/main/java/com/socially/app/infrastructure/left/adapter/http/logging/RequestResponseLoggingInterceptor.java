package com.socially.app.infrastructure.left.adapter.http.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class RequestResponseLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_RECEIVED_MESSAGE = "Request received";
  private static final String RESPONSE_SENT_MESSAGE = "Response sent";

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    log.info(
        REQUEST_RECEIVED_MESSAGE,
        StructuredArguments.keyValue("requestUri", request.getRequestURI()),
        StructuredArguments.keyValue("body", extractRequestBody(request)));
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    log.info(
        RESPONSE_SENT_MESSAGE,
        StructuredArguments.keyValue("requestUri", request.getRequestURI()),
        StructuredArguments.keyValue("body", extractResponseBody(response)));
  }

  private String extractRequestBody(HttpServletRequest request) {
    if (request instanceof ContentCachingRequestWrapper wrapper) {
      return new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    return "";
  }

  private String extractResponseBody(HttpServletResponse response) {
    if (response instanceof ContentCachingResponseWrapper wrapper) {
      return new String(wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    return "";
  }
}
