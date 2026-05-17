package com.socially.auth.callback.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class AuthorizationCodeExchangeFailedException extends RuntimeException {
  public AuthorizationCodeExchangeFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
