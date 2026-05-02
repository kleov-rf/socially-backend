package com.socially.app.infrastructure.left.adapter.http.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

class HttpBodyCachingFilterTest {

  private final HttpBodyCachingFilter filter = new HttpBodyCachingFilter();

  @Test
  void doFilterInternal_setsEntityIdFromCreateBodyAndPreservesRequestReplay() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/donations");
    request.setContent(
        "{\"id\":\"donation-123\",\"title\":\"title\",\"description\":\"desc\"}"
            .getBytes(StandardCharsets.UTF_8));
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(
        request,
        response,
        (wrappedRequest, wrappedResponse) -> {
          assertTrue(wrappedRequest instanceof CachedBodyHttpServletRequest);
          assertTrue(wrappedResponse instanceof ContentCachingResponseWrapper);
          assertEquals("donation-123", MDC.get("entity_id"));

          String firstRead =
              new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
          String secondRead =
              new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
          assertEquals(firstRead, secondRead);

          wrappedResponse.getWriter().write("response-body");
        });

    assertEquals("response-body", response.getContentAsString());
    assertNull(MDC.get("entity_id"));
  }

  @Test
  void doFilterInternal_doesNotSetEntityIdWhenCreateBodyIdIsMissing() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/donations");
    request.setContent(
        "{\"title\":\"title\",\"description\":\"desc\"}".getBytes(StandardCharsets.UTF_8));
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(
        request,
        response,
        (wrappedRequest, wrappedResponse) -> {
          assertNull(MDC.get("entity_id"));
          wrappedResponse.getWriter().write("response-body");
        });

    assertEquals("response-body", response.getContentAsString());
    assertNull(MDC.get("entity_id"));
  }
}
