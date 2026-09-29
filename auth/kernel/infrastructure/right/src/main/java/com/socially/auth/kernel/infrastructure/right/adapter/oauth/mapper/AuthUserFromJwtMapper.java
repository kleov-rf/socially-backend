package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.socially.auth.kernel.domain.AuthUser;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public final class AuthUserFromJwtMapper {

  private final AuthUserEmailMapper authUserEmailMapper;

  public AuthUser fromJwt(Jwt jwt) {
    return AuthUser.create(
            blankToNull(jwt.getClaimAsString("iss")),
            blankToNull(jwt.getClaimAsString("sub")),
            authUserEmailMapper.resolveEmail(jwt))
        .withGivenName(blankToOptional(jwt.getClaimAsString("given_name")))
        .withFamilyName(blankToOptional(jwt.getClaimAsString("family_name")));
  }

  private static Optional<String> blankToOptional(String value) {
    return StringUtils.hasText(value) ? Optional.of(value.trim()) : Optional.empty();
  }

  private static String blankToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
