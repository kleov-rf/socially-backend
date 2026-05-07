package com.socially.auth.login.application.security;

import com.socially.auth.kernel.domain.CookieInstruction;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieFactory {
  public CookieInstruction oauthTransientCookie(String name, String value) {
    return new CookieInstruction(name, value, Duration.ofMinutes(5).toSeconds());
  }
}
