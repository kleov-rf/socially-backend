package com.socially.auth.refresh.application.exception;

public class InvalidRefreshedIdTokenException extends RuntimeException {
  public InvalidRefreshedIdTokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
