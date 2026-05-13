package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
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
