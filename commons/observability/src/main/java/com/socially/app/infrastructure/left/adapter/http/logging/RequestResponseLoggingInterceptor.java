package com.socially.app.infrastructure.left.adapter.http.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class RequestResponseLoggingInterceptor implements HandlerInterceptor {

  private static final String REQUEST_RECEIVED_MESSAGE = "Request received";
  private static final String RESPONSE_SENT_MESSAGE = "Response sent";
  private static final String OPERATION_MDC_KEY = "operation";
  private static final String ENTITY_ID_MDC_KEY = "entity_id";
  private static final String ENTITY_ID_PATH_VARIABLE_NAME = "id";

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    resolveOperation(handler).ifPresent(operation -> MDC.put(OPERATION_MDC_KEY, operation));
    resolveEntityId(request).ifPresent(entityId -> MDC.put(ENTITY_ID_MDC_KEY, entityId));

    log.info(
        REQUEST_RECEIVED_MESSAGE,
        StructuredArguments.keyValue("uri", request.getRequestURI()),
        StructuredArguments.keyValue("method", request.getMethod()),
        StructuredArguments.keyValue("body", extractRequestBody(request)));
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    try {
      log.info(
          RESPONSE_SENT_MESSAGE,
          StructuredArguments.keyValue("uri", request.getRequestURI()),
          StructuredArguments.keyValue("method", request.getMethod()),
          StructuredArguments.keyValue("status", response.getStatus()),
          StructuredArguments.keyValue("body", extractResponseBody(response)));
    } finally {
      MDC.remove(OPERATION_MDC_KEY);
      MDC.remove(ENTITY_ID_MDC_KEY);
    }
  }

  private Optional<String> resolveOperation(Object handler) {
    if (handler instanceof HandlerMethod handlerMethod) {
      LogOperation logOperation = handlerMethod.getBeanType().getAnnotation(LogOperation.class);
      if (logOperation != null) {
        return Optional.of(logOperation.value());
      }
    }

    return Optional.empty();
  }

  @SuppressWarnings("unchecked")
  private Optional<String> resolveEntityId(HttpServletRequest request) {
    Object uriTemplateVariables =
        request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (uriTemplateVariables instanceof Map<?, ?> variablesMap) {
      Object entityId = variablesMap.get(ENTITY_ID_PATH_VARIABLE_NAME);
      if (entityId instanceof String entityIdValue && !entityIdValue.isBlank()) {
        return Optional.of(entityIdValue);
      }
    }

    return Optional.empty();
  }

  private String extractRequestBody(HttpServletRequest request) {
    if (request instanceof CachedBodyHttpServletRequest wrapper) {
      return wrapper.getCachedBodyAsString();
    }

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
