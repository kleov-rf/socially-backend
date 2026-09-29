package com.socially.auth.kernel.domain;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record AuthUser(
    String issuer,
    String subject,
    String email,
    Optional<String> givenName,
    Optional<String> familyName) {

  public static AuthUser create(String issuer, String subject, String email) {
    return new AuthUser(issuer, subject, email, Optional.empty(), Optional.empty());
  }

  public AuthUser withGivenName(Optional<String> givenName) {
    return new AuthUser(issuer, subject, email, givenName, familyName);
  }

  public AuthUser withFamilyName(Optional<String> familyName) {
    return new AuthUser(issuer, subject, email, givenName, familyName);
  }
}
