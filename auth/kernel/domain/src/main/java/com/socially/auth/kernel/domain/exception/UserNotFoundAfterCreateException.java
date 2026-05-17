package com.socially.auth.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserNotFoundAfterCreateException extends RuntimeException {
  public UserNotFoundAfterCreateException() {
    super("User not found after create");
  }
}
