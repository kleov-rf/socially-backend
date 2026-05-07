package com.socially.auth.callback.application.exception;

public class AuthorizationCodeExchangeFailedException extends RuntimeException {
  public AuthorizationCodeExchangeFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
