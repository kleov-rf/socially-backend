package com.socially.auth.refresh.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidRefreshedIdTokenException extends RuntimeException {
  public InvalidRefreshedIdTokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
