package com.socially.auth.callback.application.exception;

public class InvalidOAuthStateException extends RuntimeException {
  public InvalidOAuthStateException() {
    super("Invalid OAuth state parameter");
  }
}
