package com.socially.auth.kernel.domain.exception;

public final class UnauthenticatedRequestException extends RuntimeException {
  public UnauthenticatedRequestException() {
    super("Unauthenticated request");
  }
}
