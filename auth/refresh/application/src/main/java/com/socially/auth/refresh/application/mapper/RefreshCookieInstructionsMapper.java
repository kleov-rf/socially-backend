package com.socially.auth.refresh.application.mapper;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class RefreshCookieInstructionsMapper {
  private static final long REFRESH_COOKIE_MAX_AGE_SECONDS = 60L * 60L * 24L;

  private final AuthProperties authProperties;

  @Nullable
  public CookieInstruction toCookieInstruction(OAuthTokenResponse tokenResponse) {
    if (!StringUtils.hasText(tokenResponse.refreshToken())) {
      return null;
    }
    return new CookieInstruction(
        authProperties.refreshCookieName(),
        tokenResponse.refreshToken(),
        REFRESH_COOKIE_MAX_AGE_SECONDS);
  }
}
