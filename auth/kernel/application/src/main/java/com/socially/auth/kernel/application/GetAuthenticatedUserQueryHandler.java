package com.socially.auth.kernel.application;

import com.socially.auth.kernel.application.port.left.GetAuthenticatedUserUseCase;
import com.socially.auth.kernel.domain.exception.UnauthenticatedRequestException;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserFromJwtMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.AuthenticatedUserResolver;
import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetAuthenticatedUserQueryHandler implements GetAuthenticatedUserUseCase {
  private final AuthenticatedUserResolver authenticatedUserResolver;
  private final AuthUserFromJwtMapper authUserFromJwtMapper;

  @Override
  public User execute(Principal principal) {
    if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return authenticatedUserResolver.resolveExisting(
          authUserFromJwtMapper.fromJwt(jwtAuthenticationToken.getToken()));
    }
    throw new UnauthenticatedRequestException();
  }
}
