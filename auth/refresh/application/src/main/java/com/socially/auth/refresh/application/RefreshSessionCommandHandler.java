package com.socially.auth.refresh.application;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.exception.UserNotFoundAfterCreateException;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthResultMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.mapper.AuthUserToCreateUserCommandMapper;
import com.socially.auth.refresh.application.exception.InvalidRefreshedIdTokenException;
import com.socially.auth.refresh.application.exception.MissingRefreshSessionException;
import com.socially.auth.refresh.application.mapper.RefreshCookieInstructionsMapper;
import com.socially.auth.refresh.application.output.RefreshSessionCommandResult;
import com.socially.auth.refresh.application.port.left.RefreshSessionUseCase;
import com.socially.auth.refresh.domain.port.right.RefreshTokenExchangeOAuthClient;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
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
  private final AuthUserToCreateUserCommandMapper authUserToCreateUserCommandMapper;
  private final CreateUserUseCase createUserUseCase;
  private final FindUserByEmailUseCase findUserByEmailUseCase;

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
    var createUserCommand = authUserToCreateUserCommandMapper.toCommand(authResult.user());
    createUserUseCase.execute(createUserCommand);
    var user =
        findUserByEmailUseCase
            .execute(new FindUserByEmailQuery(createUserCommand.email()))
            .orElseThrow(UserNotFoundAfterCreateException::new);
    return new RefreshSessionCommandResult(authResult, user, cookieInstruction);
  }

  private AuthResult decodeAuthResult(OAuthTokenResponse tokenResponse) {
    try {
      return authResultMapper.toAuthResult(tokenResponse);
    } catch (IllegalArgumentException exception) {
      throw new InvalidRefreshedIdTokenException(exception.getMessage(), exception);
    }
  }
}
