package com.socially.auth.me.application.exception;

public final class MeUserNotFoundException extends RuntimeException {
  public MeUserNotFoundException() {
    super("User not found");
  }
}
