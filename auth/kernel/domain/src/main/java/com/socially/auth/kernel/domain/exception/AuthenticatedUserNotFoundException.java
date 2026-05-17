package com.socially.auth.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public final class AuthenticatedUserNotFoundException extends RuntimeException {
  public AuthenticatedUserNotFoundException() {
    super("User not found");
  }
}
