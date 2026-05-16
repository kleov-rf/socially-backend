package com.socially.auth.me.application;

import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserFromJwtMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.AuthenticatedUserResolver;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetCurrentAuthUserQueryHandler implements GetCurrentAuthUserUseCase {
  private final AuthenticatedUserResolver authenticatedUserResolver;
  private final AuthUserFromJwtMapper authUserFromJwtMapper;

  @Override
  public User execute(Principal principal) {
    if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return authenticatedUserResolver.resolve(
          authUserFromJwtMapper.fromJwt(jwtAuthenticationToken.getToken()));
    }
    throw new UnauthenticatedRequestException();
  }
}
