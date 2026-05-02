package com.socially.app.infrastructure.left.adapter.http.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

class HttpBodyCachingFilterTest {

  private final HttpBodyCachingFilter filter = new HttpBodyCachingFilter();

  @Test
  void doFilterInternal_wrapsRequestAndResponseAndCopiesResponseBody() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/donations");
    request.setContent("request-body".getBytes(StandardCharsets.UTF_8));
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(
        request,
        response,
        (wrappedRequest, wrappedResponse) -> {
          assertTrue(wrappedRequest instanceof ContentCachingRequestWrapper);
          assertTrue(wrappedResponse instanceof ContentCachingResponseWrapper);

          wrappedRequest.getInputStream().readAllBytes();
          wrappedResponse.getWriter().write("response-body");
        });

    assertEquals("response-body", response.getContentAsString());
  }
}
