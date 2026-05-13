package com.socially.auth.kernel.domain.exception;

public class UserNotFoundAfterCreateException extends RuntimeException {
  public UserNotFoundAfterCreateException() {
    super("User not found after create");
  }
}
