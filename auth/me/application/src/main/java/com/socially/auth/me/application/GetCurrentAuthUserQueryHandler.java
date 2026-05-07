package com.socially.auth.me.application;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import java.security.Principal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GetCurrentAuthUserQueryHandler implements GetCurrentAuthUserUseCase {
  @Override
  public AuthUser execute(Principal principal) {
    if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return userFromJwt(jwtAuthenticationToken.getToken());
    }
    throw new UnauthenticatedRequestException();
  }

  private AuthUser userFromJwt(Jwt jwt) {
    String givenName = jwt.getClaimAsString("given_name");
    String familyName = jwt.getClaimAsString("family_name");
    String fullName = joinNames(givenName, familyName);
    return new AuthUser(jwt.getClaimAsString("sub"), jwt.getClaimAsString("email"), fullName);
  }

  private String joinNames(String givenName, String familyName) {
    if (StringUtils.hasText(givenName) && StringUtils.hasText(familyName)) {
      return givenName + " " + familyName;
    }
    if (StringUtils.hasText(givenName)) {
      return givenName;
    }
    if (StringUtils.hasText(familyName)) {
      return familyName;
    }
    return null;
  }
}
