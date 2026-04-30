package com.socially.app.infrastructure.left.adapter.http.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

  @ExceptionHandler(Exception.class)
  public ResponseEntity<GlobalErrorResponse> handleServerException(Exception exception) {
    log.error(UNEXPECTED_SERVER_ERROR_MESSAGE, exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new GlobalErrorResponse(UNEXPECTED_SERVER_ERROR_MESSAGE));
  }
}
