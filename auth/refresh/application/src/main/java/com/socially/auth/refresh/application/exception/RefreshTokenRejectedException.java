package com.socially.auth.refresh.application.exception;

public class RefreshTokenRejectedException extends RuntimeException {
  public RefreshTokenRejectedException(Throwable cause) {
    super("Refresh token rejected", cause);
  }
}
