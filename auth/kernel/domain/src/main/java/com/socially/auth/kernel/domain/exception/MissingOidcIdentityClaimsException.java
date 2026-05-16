package com.socially.auth.kernel.domain.exception;

public final class MissingOidcIdentityClaimsException extends RuntimeException {

  public MissingOidcIdentityClaimsException() {
    super("OIDC issuer and subject claims are required");
  }
}
