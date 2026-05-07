package com.socially.auth.callback.application.mapper;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class CallbackCookieInstructionsMapper {
  private static final long REFRESH_COOKIE_MAX_AGE_SECONDS = 60L * 60L * 24L * 30L;

  private final AuthProperties authProperties;

  public List<CookieInstruction> toCookieInstructions(OAuthTokenResponse tokenResponse) {
    List<CookieInstruction> instructions = new ArrayList<>();
    instructions.add(new CookieInstruction(authProperties.stateCookieName(), "", 0L));
    instructions.add(new CookieInstruction(authProperties.pkceCookieName(), "", 0L));

    if (StringUtils.hasText(tokenResponse.refreshToken())) {
      instructions.add(
          new CookieInstruction(
              authProperties.refreshCookieName(),
              tokenResponse.refreshToken(),
              REFRESH_COOKIE_MAX_AGE_SECONDS));
    }

    return List.copyOf(instructions);
  }
}
