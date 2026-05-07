package com.socially.auth.refresh.application.exception;

public class RefreshTokenExchangeFailedException extends RuntimeException {
  public RefreshTokenExchangeFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
