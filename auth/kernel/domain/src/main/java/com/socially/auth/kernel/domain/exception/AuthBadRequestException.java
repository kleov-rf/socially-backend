package com.socially.auth.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class AuthBadRequestException extends RuntimeException {

  public AuthBadRequestException(String message) {
    super(message);
  }
}
