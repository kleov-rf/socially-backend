package com.socially.auth.kernel.domain.exception;

public final class AuthenticatedUserNotFoundException extends RuntimeException {
  public AuthenticatedUserNotFoundException() {
    super("User not found");
  }
}
