package com.socially.auth.logout.application;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.logout.application.output.LogoutResult;
import com.socially.auth.logout.application.port.left.LogoutUseCase;
import com.socially.auth.logout.domain.port.right.RefreshTokenRevocationOAuthClient;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class LogoutCommandHandler implements LogoutUseCase {
  private final AuthProperties authProperties;
  private final RefreshTokenRevocationOAuthClient refreshTokenRevocationOAuthClient;

  @Override
  public LogoutResult execute(Map<String, String> requestCookies) {
    String refreshToken = requestCookies.get(authProperties.refreshCookieName());
    if (StringUtils.hasText(refreshToken)) {
      refreshTokenRevocationOAuthClient.revokeRefreshToken(refreshToken);
    }
    return new LogoutResult(
        List.of(
            new CookieInstruction(authProperties.refreshCookieName(), "", 0L),
            new CookieInstruction(authProperties.stateCookieName(), "", 0L),
            new CookieInstruction(authProperties.pkceCookieName(), "", 0L)));
  }
}
