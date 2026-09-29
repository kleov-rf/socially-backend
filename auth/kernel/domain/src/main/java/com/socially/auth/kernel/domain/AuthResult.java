package com.socially.auth.kernel.domain;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AuthResult(
    String accessToken, String tokenType, AuthUser user, Optional<Long> expiresIn) {

  public static AuthResult create(String accessToken, String tokenType, AuthUser user) {
    return new AuthResult(accessToken, tokenType, user, Optional.empty());
  }

  public AuthResult withExpiresIn(Optional<Long> expiresIn) {
    return new AuthResult(accessToken, tokenType, user, expiresIn);
  }
}
