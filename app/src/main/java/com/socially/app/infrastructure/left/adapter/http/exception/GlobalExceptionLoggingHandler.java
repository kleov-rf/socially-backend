package com.socially.app.infrastructure.left.adapter.http.exception;

import com.socially.auth.kernel.domain.exception.AuthenticatedUserNotFoundException;
import com.socially.auth.me.application.exception.MeUserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionLoggingHandler {

  private static final String UNEXPECTED_SERVER_ERROR_MESSAGE = "Unexpected server error";

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<GlobalErrorResponse> handleClientException(
      IllegalArgumentException exception) {
    log.warn("Handled client error: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new GlobalErrorResponse(exception.getMessage()));
  }

  @ExceptionHandler(MeUserNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleMeUserNotFound(
      MeUserNotFoundException exception) {
    log.warn("Handled user not found error: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new GlobalErrorResponse(exception.getMessage()));
  }

  @ExceptionHandler(AuthenticatedUserNotFoundException.class)
  public ResponseEntity<GlobalErrorResponse> handleAuthenticatedUserNotFound(
      AuthenticatedUserNotFoundException exception) {
    log.warn("Handled authenticated user not found error: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new GlobalErrorResponse(exception.getMessage()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<GlobalErrorResponse> handleResponseStatusException(
      ResponseStatusException exception) {
    HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
    String message = exception.getReason() != null ? exception.getReason() : "Request failed";
    log.warn("Handled status exception {}: {}", status.value(), message);
    return ResponseEntity.status(status).body(new GlobalErrorResponse(message));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GlobalErrorResponse> handleServerException(Exception exception) {
    log.error(UNEXPECTED_SERVER_ERROR_MESSAGE, exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new GlobalErrorResponse(UNEXPECTED_SERVER_ERROR_MESSAGE));
  }
}
