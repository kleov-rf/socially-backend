package com.socially.auth.callback.application;

import com.socially.auth.callback.application.exception.InvalidOAuthStateException;
import com.socially.auth.callback.application.exception.MissingTemporaryOAuthCookiesException;
import com.socially.auth.callback.application.mapper.CallbackCookieInstructionsMapper;
import com.socially.auth.callback.application.output.CompleteOAuthCallbackOutcome;
import com.socially.auth.callback.application.port.left.CompleteOAuthCallbackUseCase;
import com.socially.auth.callback.domain.port.right.AuthorizationCodeExchangeOAuthClient;
import com.socially.auth.kernel.domain.AuthResultMapper;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class CompleteOAuthCallbackCommandHandler implements CompleteOAuthCallbackUseCase {
  private final AuthProperties authProperties;
  private final AuthorizationCodeExchangeOAuthClient authorizationCodeExchangeOAuthClient;
  private final CallbackCookieInstructionsMapper callbackCookieInstructionsMapper;
  private final AuthResultMapper authResultMapper;

  @Override
  public CompleteOAuthCallbackOutcome execute(
      String code, String state, Map<String, String> requestCookies) {
    String expectedState = requestCookies.get(authProperties.stateCookieName());
    String codeVerifier = requestCookies.get(authProperties.pkceCookieName());
    if (!StringUtils.hasText(expectedState) || !StringUtils.hasText(codeVerifier)) {
      throw new MissingTemporaryOAuthCookiesException();
    }

    if (!expectedState.equals(state)) {
      throw new InvalidOAuthStateException();
    }

    OAuthTokenResponse tokenResponse =
        authorizationCodeExchangeOAuthClient.exchangeAuthorizationCode(code, codeVerifier);

    var cookieInstructions = callbackCookieInstructionsMapper.toCookieInstructions(tokenResponse);
    var authResult = authResultMapper.toAuthResult(tokenResponse);
    return new CompleteOAuthCallbackOutcome(authResult, cookieInstructions);
  }
}
