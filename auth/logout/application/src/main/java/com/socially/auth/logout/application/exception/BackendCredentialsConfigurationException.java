package com.socially.auth.logout.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BackendCredentialsConfigurationException extends RuntimeException {
  public BackendCredentialsConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
