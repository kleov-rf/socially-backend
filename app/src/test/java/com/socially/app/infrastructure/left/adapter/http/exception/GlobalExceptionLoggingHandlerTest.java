package com.socially.app.infrastructure.left.adapter.http.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.socially.auth.kernel.domain.exception.AuthenticatedUserNotFoundException;
import com.socially.auth.me.application.exception.MeUserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class GlobalExceptionLoggingHandlerTest {

  private final GlobalExceptionLoggingHandler handler = new GlobalExceptionLoggingHandler();
  private final Logger logger =
      (Logger) LoggerFactory.getLogger(GlobalExceptionLoggingHandler.class);
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
  }

  @Test
  void handleClientException_logsWarnWithoutThrowableAndReturnsBadRequest() {
    var response = handler.handleClientException(new IllegalArgumentException("Invalid input"));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input", response.getBody().message());
    assertEquals(1, appender.list.size());
    assertEquals(Level.WARN, appender.list.getFirst().getLevel());
    assertNull(appender.list.getFirst().getThrowableProxy());
  }

  @Test
  void handleServerException_logsErrorWithThrowableAndReturnsInternalServerError() {
    RuntimeException exception = new RuntimeException("boom");

    var response = handler.handleServerException(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("Unexpected server error", response.getBody().message());
    assertEquals(1, appender.list.size());
    assertEquals(Level.ERROR, appender.list.getFirst().getLevel());
    assertNotNull(appender.list.getFirst().getThrowableProxy());
  }

  @Test
  void handleResponseStatusException_returnsOriginalStatusAndReason() {
    var response =
        handler.handleResponseStatusException(
            new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated request"));

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Unauthenticated request", response.getBody().message());
  }

  @Test
  void handleMeUserNotFound_returnsNotFoundAndMessage() {
    var response = handler.handleMeUserNotFound(new MeUserNotFoundException());

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("User not found", response.getBody().message());
    assertEquals(1, appender.list.size());
    assertEquals(Level.WARN, appender.list.getFirst().getLevel());
  }

  @Test
  void handleAuthenticatedUserNotFound_returnsUnauthorizedAndMessage() {
    var response =
        handler.handleAuthenticatedUserNotFound(new AuthenticatedUserNotFoundException());

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("User not found", response.getBody().message());
    assertEquals(1, appender.list.size());
    assertEquals(Level.WARN, appender.list.getFirst().getLevel());
  }
}
