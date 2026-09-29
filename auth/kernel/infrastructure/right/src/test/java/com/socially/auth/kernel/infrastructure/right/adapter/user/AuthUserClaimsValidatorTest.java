package com.socially.auth.kernel.infrastructure.right.adapter.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.AuthBadRequestException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthUserClaimsValidatorTest {

  private final AuthUserClaimsValidator validator = new AuthUserClaimsValidator();

  @Test
  void requireIssuerAndSubject_should_not_throw_when_issuer_and_subject_are_present() {
    AuthUser authUser =
        AuthUser.create("https://idp.example", "sub-1", "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    assertDoesNotThrow(() -> validator.requireIssuerAndSubject(authUser));
  }

  @Test
  void requireIssuerAndSubject_should_throw_when_issuer_is_missing() {
    AuthUser authUser =
        AuthUser.create(null, "sub-1", "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    AuthBadRequestException exception =
        assertThrows(
            AuthBadRequestException.class, () -> validator.requireIssuerAndSubject(authUser));

    assertEquals("OIDC issuer and subject claims are required", exception.getMessage());
  }

  @Test
  void requireIssuerAndSubject_should_throw_when_subject_is_missing() {
    AuthUser authUser =
        AuthUser.create("https://idp.example", null, "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    AuthBadRequestException exception =
        assertThrows(
            AuthBadRequestException.class, () -> validator.requireIssuerAndSubject(authUser));

    assertEquals("OIDC issuer and subject claims are required", exception.getMessage());
  }

  @Test
  void requireIssuerAndSubject_should_throw_when_issuer_is_blank() {
    AuthUser authUser =
        AuthUser.create("   ", "sub-1", "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    assertThrows(AuthBadRequestException.class, () -> validator.requireIssuerAndSubject(authUser));
  }

  @Test
  void requireIssuerAndSubject_should_throw_when_subject_is_blank() {
    AuthUser authUser =
        AuthUser.create("https://idp.example", "   ", "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    assertThrows(AuthBadRequestException.class, () -> validator.requireIssuerAndSubject(authUser));
  }
}
