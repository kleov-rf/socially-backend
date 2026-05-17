package com.socially.auth.kernel.infrastructure.right.adapter.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.AuthBadRequestException;
import org.junit.jupiter.api.Test;

class AuthUserClaimsValidatorTest {

  private final AuthUserClaimsValidator validator = new AuthUserClaimsValidator();

  @Test
  void requireIssuerAndSubject_should_not_throw_when_issuer_and_subject_are_present() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "sub-1", "user@example.com", "Jane", "Doe");

    assertDoesNotThrow(() -> validator.requireIssuerAndSubject(authUser));
  }

  @Test
  void requireIssuerAndSubject_should_throw_when_issuer_is_missing() {
    AuthUser authUser = new AuthUser(null, "sub-1", "user@example.com", "Jane", "Doe");

    AuthBadRequestException exception =
        assertThrows(
            AuthBadRequestException.class, () -> validator.requireIssuerAndSubject(authUser));

    assertEquals("OIDC issuer and subject claims are required", exception.getMessage());
  }

  @Test
  void requireIssuerAndSubject_should_throw_when_subject_is_missing() {
    AuthUser authUser =
        new AuthUser("https://idp.example", null, "user@example.com", "Jane", "Doe");

    AuthBadRequestException exception =
        assertThrows(
            AuthBadRequestException.class, () -> validator.requireIssuerAndSubject(authUser));

    assertEquals("OIDC issuer and subject claims are required", exception.getMessage());
  }

  @Test
  void requireIssuerAndSubject_should_throw_when_issuer_is_blank() {
    AuthUser authUser = new AuthUser("   ", "sub-1", "user@example.com", "Jane", "Doe");

    assertThrows(AuthBadRequestException.class, () -> validator.requireIssuerAndSubject(authUser));
  }

  @Test
  void requireIssuerAndSubject_should_throw_when_subject_is_blank() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "   ", "user@example.com", "Jane", "Doe");

    assertThrows(AuthBadRequestException.class, () -> validator.requireIssuerAndSubject(authUser));
  }
}
