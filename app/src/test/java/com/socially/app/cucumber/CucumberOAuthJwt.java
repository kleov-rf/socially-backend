package com.socially.app.cucumber;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class CucumberOAuthJwt {

  private CucumberOAuthJwt() {}

  public static RequestPostProcessor postProcessor() {
    return jwt()
        .jwt(
            builder ->
                builder
                    .subject("auth-user-1")
                    .claim("email", "auth.user@example.com")
                    .claim("given_name", "Auth")
                    .claim("family_name", "User"));
  }
}
