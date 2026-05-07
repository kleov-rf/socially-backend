package com.socially.auth.login.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;
import org.junit.jupiter.api.Test;

class OAuthPkceServiceTest {

  private static final String PKCE_VERIFIER = "pkce-test-verifier";
  private static final String PKCE_CHALLENGE = "aNsHKVxnyY9miDbOaa5GLREr8C-xfjIdLs44tdhMKgM";

  private final OAuthPkceService sut = new OAuthPkceService();

  @Test
  void generateCodeChallenge_should_return_expected_challenge_for_known_verifier() {
    String actual = sut.generateCodeChallenge(PKCE_VERIFIER);

    assertEquals(PKCE_CHALLENGE, actual);
  }

  @Test
  void generateRandomToken_should_return_url_safe_base64_token() {
    String actual = sut.generateRandomToken();

    assertNotNull(actual);
    assertTrue(actual.matches("^[A-Za-z0-9_-]+$"));
    assertEquals(32, Base64.getUrlDecoder().decode(actual).length);
  }

  @Test
  void generateRandomToken_should_return_token_without_padding() {
    String actual = sut.generateRandomToken();

    assertFalse(actual.contains("="));
    assertEquals(43, actual.length());
  }
}
