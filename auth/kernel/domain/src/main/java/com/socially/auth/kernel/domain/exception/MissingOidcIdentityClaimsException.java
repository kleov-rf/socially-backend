package com.socially.auth.kernel.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class MissingOidcIdentityClaimsException extends RuntimeException {

  public MissingOidcIdentityClaimsException() {
    super("OIDC issuer and subject claims are required");
  }
}
