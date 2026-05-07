package com.socially.auth.refresh.application;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthResultMapper;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.refresh.application.exception.InvalidRefreshedIdTokenException;
import com.socially.auth.refresh.application.exception.MissingRefreshSessionException;
import com.socially.auth.refresh.application.mapper.RefreshCookieInstructionsMapper;
import com.socially.auth.refresh.application.output.RefreshSessionCommandResult;
import com.socially.auth.refresh.application.port.left.RefreshSessionUseCase;
import com.socially.auth.refresh.domain.port.right.RefreshTokenExchangeOAuthClient;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class RefreshSessionCommandHandler implements RefreshSessionUseCase {
  private final AuthProperties authProperties;
  private final RefreshTokenExchangeOAuthClient refreshTokenExchangeOAuthClient;
  private final RefreshCookieInstructionsMapper refreshCookieInstructionsMapper;
  private final AuthResultMapper authResultMapper;

  @Override
  public RefreshSessionCommandResult execute(Map<String, String> requestCookies) {
    String refreshToken = requestCookies.get(authProperties.refreshCookieName());
    if (!StringUtils.hasText(refreshToken)) {
      throw new MissingRefreshSessionException();
    }

    OAuthTokenResponse tokenResponse =
        refreshTokenExchangeOAuthClient.exchangeRefreshToken(refreshToken);

    CookieInstruction cookieInstruction =
        refreshCookieInstructionsMapper.toCookieInstruction(tokenResponse);
    var authResult = decodeAuthResult(tokenResponse);
    return new RefreshSessionCommandResult(authResult, cookieInstruction);
  }

  private AuthResult decodeAuthResult(OAuthTokenResponse tokenResponse) {
    try {
      return authResultMapper.toAuthResult(tokenResponse);
    } catch (IllegalArgumentException exception) {
      throw new InvalidRefreshedIdTokenException(exception.getMessage(), exception);
    }
  }
}
