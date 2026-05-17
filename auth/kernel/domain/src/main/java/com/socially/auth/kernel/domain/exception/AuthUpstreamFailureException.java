package com.socially.auth.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public final class AuthUpstreamFailureException extends RuntimeException {

  public AuthUpstreamFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
