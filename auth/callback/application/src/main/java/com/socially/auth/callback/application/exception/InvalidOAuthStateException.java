package com.socially.auth.callback.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOAuthStateException extends RuntimeException {
  public InvalidOAuthStateException() {
    super("Invalid OAuth state parameter");
  }
}
