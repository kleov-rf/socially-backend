package com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper;

import com.socially.auth.kernel.domain.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public final class AuthUserFromJwtMapper {

  private final AuthUserEmailMapper authUserEmailMapper;

  public AuthUser fromJwt(Jwt jwt) {
    return new AuthUser(
        blankToNull(jwt.getClaimAsString("iss")),
        blankToNull(jwt.getClaimAsString("sub")),
        authUserEmailMapper.resolveEmail(jwt),
        blankToNull(jwt.getClaimAsString("given_name")),
        blankToNull(jwt.getClaimAsString("family_name")));
  }

  private static String blankToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
