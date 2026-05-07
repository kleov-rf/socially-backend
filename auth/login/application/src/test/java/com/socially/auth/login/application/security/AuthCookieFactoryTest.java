package com.socially.auth.login.application.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthCookieFactoryTest {

  private final AuthCookieFactory factory = new AuthCookieFactory();

  @Test
  void oauthTransientCookie_should_return_instruction_with_name() {
    var instruction = factory.oauthTransientCookie("my_name", null);

    assertEquals("my_name", instruction.name());
  }

  @Test
  void oauthTransientCookie_should_return_instruction_with_value() {
    var instruction = factory.oauthTransientCookie(null, "my_value");

    assertEquals("my_value", instruction.value());
  }

  @Test
  void oauthTransientCookie_should_return_instruction_with_five_minutes_max_age() {
    var instruction = factory.oauthTransientCookie("n", "v");

    assertEquals(300L, instruction.maxAgeSeconds());
  }
}
