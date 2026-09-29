package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class AuthResultMapper {
  private final AuthUserMapper authUserMapper;

  public AuthResult toAuthResult(OAuthTokenResponse tokenResponse) {
    AuthUser user = authUserMapper.fromIdToken(tokenResponse.idToken());
    Optional<Long> expiresIn =
        tokenResponse.expiresIn() != null
            ? Optional.of(tokenResponse.expiresIn())
            : Optional.empty();
    return AuthResult.create(tokenResponse.accessToken(), tokenResponse.tokenType(), user)
        .withExpiresIn(expiresIn);
  }
}
