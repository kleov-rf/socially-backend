package com.socially.app.infrastructure.left.adapter.http.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

class RequestResponseLoggingInterceptorTest {

  private final RequestResponseLoggingInterceptor interceptor =
      new RequestResponseLoggingInterceptor();
  private final Logger logger =
      (Logger) LoggerFactory.getLogger(RequestResponseLoggingInterceptor.class);
  private ListAppender<ILoggingEvent> appender;

  @BeforeEach
  void setUp() {
    appender = new ListAppender<>();
    appender.start();
    logger.addAppender(appender);
  }

  @AfterEach
  void tearDown() {
    logger.detachAppender(appender);
    appender.stop();
    MDC.clear();
  }

  @Test
  void preHandle_logsRequestReceivedWithStructuredRequestBodyAndReturnsTrue() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/donations");
    request.setContent("request-payload".getBytes(StandardCharsets.UTF_8));
    ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request, 1024);
    cachedRequest.getInputStream().readAllBytes();
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean shouldProceed = interceptor.preHandle(cachedRequest, response, new Object());

    assertTrue(shouldProceed);
    assertEquals(1, appender.list.size());
    ILoggingEvent event = appender.list.getFirst();
    assertEquals(Level.INFO, event.getLevel());
    assertEquals("Request received", event.getFormattedMessage());
    assertEquals(3, event.getArgumentArray().length);
    assertEquals("uri=/api/donations", event.getArgumentArray()[0].toString());
    assertEquals("method=GET", event.getArgumentArray()[1].toString());
    assertEquals("body=request-payload", event.getArgumentArray()[2].toString());
    assertFalse(event.hasCallerData());
  }

  @Test
  void logsOperationInMdcForAnnotatedControllerHandler() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/donations");
    request.setContent("request-payload".getBytes(StandardCharsets.UTF_8));
    ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request, 1024);
    cachedRequest.getInputStream().readAllBytes();
    ContentCachingResponseWrapper cachedResponse =
        new ContentCachingResponseWrapper(new MockHttpServletResponse());
    cachedResponse.getWriter().write("response-payload");
    cachedResponse.setStatus(200);
    HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), "endpoint");

    interceptor.preHandle(cachedRequest, cachedResponse, handlerMethod);
    interceptor.afterCompletion(cachedRequest, cachedResponse, handlerMethod, null);

    assertEquals(2, appender.list.size());
    assertEquals("CREATE_DONATION", appender.list.get(0).getMDCPropertyMap().get("operation"));
    assertEquals("CREATE_DONATION", appender.list.get(1).getMDCPropertyMap().get("operation"));
    assertNull(MDC.get("operation"));
  }

  @Test
  void doesNotSetOperationMdcWhenAnnotationIsMissing() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/donations");
    request.setContent("request-payload".getBytes(StandardCharsets.UTF_8));
    ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request, 1024);
    cachedRequest.getInputStream().readAllBytes();
    ContentCachingResponseWrapper cachedResponse =
        new ContentCachingResponseWrapper(new MockHttpServletResponse());
    cachedResponse.getWriter().write("response-payload");
    cachedResponse.setStatus(200);
    HandlerMethod handlerMethod = new HandlerMethod(new UnannotatedController(), "endpoint");

    interceptor.preHandle(cachedRequest, cachedResponse, handlerMethod);
    interceptor.afterCompletion(cachedRequest, cachedResponse, handlerMethod, null);

    assertEquals(2, appender.list.size());
    assertNull(appender.list.get(0).getMDCPropertyMap().get("operation"));
    assertNull(appender.list.get(1).getMDCPropertyMap().get("operation"));
    assertNull(MDC.get("operation"));
  }

  @Test
  void afterCompletion_logsResponseSentWithStructuredResponseBody() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/donations");
    ContentCachingRequestWrapper cachedRequest = new ContentCachingRequestWrapper(request, 1024);
    ContentCachingResponseWrapper cachedResponse =
        new ContentCachingResponseWrapper(new MockHttpServletResponse());
    cachedResponse.getWriter().write("response-payload");
    cachedResponse.setStatus(201);

    interceptor.afterCompletion(cachedRequest, cachedResponse, new Object(), null);

    assertEquals(1, appender.list.size());
    ILoggingEvent event = appender.list.getFirst();
    assertEquals(Level.INFO, event.getLevel());
    assertEquals("Response sent", event.getFormattedMessage());
    assertEquals(4, event.getArgumentArray().length);
    assertEquals("uri=/api/donations", event.getArgumentArray()[0].toString());
    assertEquals("method=POST", event.getArgumentArray()[1].toString());
    assertEquals("status=201", event.getArgumentArray()[2].toString());
    assertEquals("body=response-payload", event.getArgumentArray()[3].toString());
  }

  @LogOperation("CREATE_DONATION")
  private static class AnnotatedController {
    public void endpoint() {}
  }

  private static class UnannotatedController {
    public void endpoint() {}
  }
}
