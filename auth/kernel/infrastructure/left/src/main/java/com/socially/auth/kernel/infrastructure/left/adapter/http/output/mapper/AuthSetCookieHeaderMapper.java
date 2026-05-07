package com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthSetCookieHeaderMapper {
  private final AuthProperties authProperties;

  public String toSetCookieHeader(CookieInstruction instruction) {
    return ResponseCookie.from(instruction.name(), instruction.value())
        .httpOnly(true)
        .secure(authProperties.cookiesSecure())
        .sameSite("Lax")
        .path("/")
        .maxAge(instruction.maxAgeSeconds())
        .build()
        .toString();
  }
}
