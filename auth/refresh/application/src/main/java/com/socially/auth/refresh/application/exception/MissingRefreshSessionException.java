package com.socially.auth.refresh.application.exception;

public class MissingRefreshSessionException extends RuntimeException {
  public MissingRefreshSessionException() {
    super("Missing refresh session");
  }
}
