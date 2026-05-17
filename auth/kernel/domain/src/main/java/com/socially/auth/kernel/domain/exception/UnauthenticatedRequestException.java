package com.socially.auth.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public final class UnauthenticatedRequestException extends RuntimeException {
  public UnauthenticatedRequestException() {
    super("Unauthenticated request");
  }
}
