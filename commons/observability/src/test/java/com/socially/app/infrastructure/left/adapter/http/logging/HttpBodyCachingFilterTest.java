package com.socially.app.infrastructure.left.adapter.http.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

class HttpBodyCachingFilterTest {

  private final HttpBodyCachingFilter filter = new HttpBodyCachingFilter();
  private final Logger logger = (Logger) LoggerFactory.getLogger(HttpBodyCachingFilter.class);
  private ListAppender<ILoggingEvent> appender;
  private Level originalLevel;

  @BeforeEach
  void setUp() {
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
    originalLevel = logger.getLevel();
    logger.setLevel(Level.DEBUG);
    MDC.clear();
  }

  @AfterEach
  void tearDown() {
    logger.detachAppender(appender);
    appender.stop();
    logger.setLevel(originalLevel);
    MDC.clear();
  }

  @Test
  void doFilterInternal_should_add_entity_id_field_from_request_body_id() throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "{\"id\":\"donation-123\",\"title\":\"title\",\"description\":\"desc\"}",
        (wrappedRequest, wrappedResponse) -> assertEquals("donation-123", MDC.get("entity_id")));
  }

  @Test
  void doFilterInternal_should_not_add_entity_id_field_when_http_method_is_not_post()
      throws Exception {
    executeFilter(
        "GET",
        "/api/donations",
        "{\"id\":\"donation-123\"}",
        (wrappedRequest, wrappedResponse) -> assertNull(MDC.get("entity_id")));
  }

  @Test
  void doFilterInternal_should_not_add_entity_id_field_when_uri_is_not_create_donation_uri()
      throws Exception {
    executeFilter(
        "POST",
        "/api/other",
        "{\"id\":\"donation-123\"}",
        (wrappedRequest, wrappedResponse) -> assertNull(MDC.get("entity_id")));
  }

  @Test
  void doFilterInternal_should_not_add_entity_id_field_when_body_is_empty() throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "",
        (wrappedRequest, wrappedResponse) -> assertNull(MDC.get("entity_id")));
  }

  @Test
  void doFilterInternal_should_not_add_entity_id_field_when_id_not_present_in_body()
      throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "{\"title\":\"title\",\"description\":\"desc\"}",
        (wrappedRequest, wrappedResponse) -> assertNull(MDC.get("entity_id")));
  }

  @Test
  void doFilterInternal_should_not_add_entity_id_field_when_id_is_blank() throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "{\"id\":\"   \"}",
        (wrappedRequest, wrappedResponse) -> assertNull(MDC.get("entity_id")));
  }

  @Test
  void doFilterInternal_should_log_debug_with_exception_when_reading_body_fails() throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "{\"id\":",
        (wrappedRequest, wrappedResponse) -> {
          // no-op
        });

    ILoggingEvent debugEvent =
        appender.list.stream()
            .filter(event -> event.getLevel() == Level.DEBUG)
            .filter(
                event ->
                    "Unable to parse create donation body for entity_id MDC"
                        .equals(event.getFormattedMessage()))
            .findFirst()
            .orElse(null);

    assertNotNull(debugEvent);
    assertNotNull(debugEvent.getThrowableProxy());
  }

  @Test
  void doFilterInternal_should_not_add_entity_id_field_when_reading_body_fails() throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "{\"id\":",
        (wrappedRequest, wrappedResponse) -> assertNull(MDC.get("entity_id")));
  }

  @Test
  void doFilterInternal_should_call_filter_chain_with_wrapped_request() throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "{\"id\":\"donation-123\"}",
        (wrappedRequest, wrappedResponse) ->
            assertTrue(wrappedRequest instanceof CachedBodyHttpServletRequest));
  }

  @Test
  void doFilterInternal_should_call_filter_chain_with_wrapped_response() throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "{\"id\":\"donation-123\"}",
        (wrappedRequest, wrappedResponse) ->
            assertTrue(wrappedResponse instanceof ContentCachingResponseWrapper));
  }

  @Test
  void doFilterInternal_should_remove_entity_id_field_after_calling_filter_chain()
      throws Exception {
    executeFilter(
        "POST",
        "/api/donations",
        "{\"id\":\"donation-123\"}",
        (wrappedRequest, wrappedResponse) -> assertEquals("donation-123", MDC.get("entity_id")));

    assertNull(MDC.get("entity_id"));
  }

  private void executeFilter(String method, String uri, String body, FilterAssertion assertion)
      throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
    request.setContent(body.getBytes(StandardCharsets.UTF_8));
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(
        request,
        response,
        (wrappedRequest, wrappedResponse) -> {
          assertion.assertFilterInputs(wrappedRequest, wrappedResponse);

          wrappedResponse.getWriter().write("response-body");
          String firstRead =
              new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
          String secondRead =
              new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
          assertEquals(firstRead, secondRead);
        });

    assertEquals("response-body", response.getContentAsString());
  }

  @FunctionalInterface
  private interface FilterAssertion {
    void assertFilterInputs(ServletRequest request, ServletResponse response) throws IOException;
  }
}
