package com.socially.auth.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public final class AuthUnauthorizedException extends RuntimeException {

  public AuthUnauthorizedException(String message) {
    super(message);
  }

  public AuthUnauthorizedException(String message, Throwable cause) {
    super(message, cause);
  }
}
