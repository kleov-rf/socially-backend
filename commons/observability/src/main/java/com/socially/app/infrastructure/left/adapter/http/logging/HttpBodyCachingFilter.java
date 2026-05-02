package com.socially.app.infrastructure.left.adapter.http.logging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
public class HttpBodyCachingFilter extends OncePerRequestFilter {

  private static final String ENTITY_ID_MDC_KEY = "entity_id";
  private static final String CREATE_DONATION_METHOD = "POST";
  private static final String CREATE_DONATION_PATH = "/api/donations";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    CachedBodyHttpServletRequest wrappedRequest =
        request instanceof CachedBodyHttpServletRequest requestWrapper
            ? requestWrapper
            : new CachedBodyHttpServletRequest(request);

    ContentCachingResponseWrapper wrappedResponse =
        response instanceof ContentCachingResponseWrapper responseWrapper
            ? responseWrapper
            : new ContentCachingResponseWrapper(response);

    boolean setEntityIdFromBody = false;

    try {
      Optional<String> entityId = resolveEntityIdFromRequestBody(wrappedRequest);
      if (entityId.isPresent()) {
        MDC.put(ENTITY_ID_MDC_KEY, entityId.get());
        setEntityIdFromBody = true;
      }
      filterChain.doFilter(wrappedRequest, wrappedResponse);
    } finally {
      if (setEntityIdFromBody) {
        MDC.remove(ENTITY_ID_MDC_KEY);
      }
      wrappedResponse.copyBodyToResponse();
    }
  }

  private Optional<String> resolveEntityIdFromRequestBody(CachedBodyHttpServletRequest request) {
    if (!CREATE_DONATION_METHOD.equalsIgnoreCase(request.getMethod())
        || !CREATE_DONATION_PATH.equals(request.getRequestURI())) {
      return Optional.empty();
    }

    String body = request.getCachedBodyAsString();
    if (body.isBlank()) {
      return Optional.empty();
    }

    try {
      JsonNode jsonNode = OBJECT_MAPPER.readTree(body);
      JsonNode idNode = jsonNode.get("id");
      if (idNode != null && !idNode.asText().isBlank()) {
        return Optional.of(idNode.asText());
      }
    } catch (IOException exception) {
      log.debug("Unable to parse create donation body for entity_id MDC", exception);
    }

    return Optional.empty();
  }
}
