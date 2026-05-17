package com.socially.auth.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public final class AuthInternalErrorException extends RuntimeException {

  public AuthInternalErrorException(String message) {
    super(message);
  }

  public AuthInternalErrorException(String message, Throwable cause) {
    super(message, cause);
  }
}
