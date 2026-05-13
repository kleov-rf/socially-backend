package com.socially.auth.me.application;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.infrastructure.right.adapter.user.mapper.AuthUserToCreateUserCommandMapper;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public class GetCurrentAuthUserQueryHandler implements GetCurrentAuthUserUseCase {
  private final AuthUserToCreateUserCommandMapper authUserToCreateUserCommandMapper;
  private final CreateUserUseCase createUserUseCase;

  @Override
  public User execute(Principal principal) {
    if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      AuthUser authUser = userFromJwt(jwtAuthenticationToken.getToken());
      return createUserUseCase.execute(authUserToCreateUserCommandMapper.toCommand(authUser));
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
