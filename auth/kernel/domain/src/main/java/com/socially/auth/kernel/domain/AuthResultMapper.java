package com.socially.auth.kernel.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class AuthResultMapper {
  private final AuthUserMapper authUserMapper;

  public AuthResult toAuthResult(OAuthTokenResponse tokenResponse) {
    AuthUser user = authUserMapper.fromIdToken(tokenResponse.idToken());
    return new AuthResult(
        tokenResponse.accessToken(), tokenResponse.tokenType(), tokenResponse.expiresIn(), user);
  }
}
