package com.socially.auth.callback.application.exception;

public class MissingTemporaryOAuthCookiesException extends RuntimeException {
  public MissingTemporaryOAuthCookiesException() {
    super("Missing temporary OAuth cookies");
  }
}
