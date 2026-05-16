package com.socially.app.cucumber;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class CucumberOAuthJwt {

  public static final String TEST_ISSUER = "https://test-idp.example";

  private CucumberOAuthJwt() {}

  public static RequestPostProcessor postProcessor() {
    return jwt()
        .jwt(
            builder ->
                builder
                    .issuer(TEST_ISSUER)
                    .subject("auth-user-1")
                    .claim("email", "auth.user@example.com")
                    .claim("given_name", "Auth")
                    .claim("family_name", "User"));
  }
}
