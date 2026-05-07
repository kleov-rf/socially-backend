package com.socially.auth.me.application.exception;

public class UnauthenticatedRequestException extends RuntimeException {
  public UnauthenticatedRequestException() {
    super("Unauthenticated request");
  }
}
