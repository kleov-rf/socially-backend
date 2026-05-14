package com.socially.auth.me.application;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.UserNotFoundAfterCreateException;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserEmailMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.mapper.AuthUserToCreateUserCommandMapper;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
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
  private final CreateUserUseCase createUserUseCase;
  private final FindUserByEmailUseCase findUserByEmailUseCase;

  private final AuthUserToCreateUserCommandMapper authUserToCreateUserCommandMapper;
  private final AuthUserEmailMapper authUserEmailMapper;

  @Override
  public User execute(Principal principal) {
    if (principal instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return getOrCreateUser(userFromJwt(jwtAuthenticationToken.getToken()));
    }
    throw new UnauthenticatedRequestException();
  }

  private User getOrCreateUser(AuthUser authUser) {
    var createUserCommand = authUserToCreateUserCommandMapper.toCommand(authUser);
    createUserUseCase.execute(createUserCommand);
    return findUserByEmailUseCase
        .execute(new FindUserByEmailQuery(createUserCommand.email()))
        .orElseThrow(UserNotFoundAfterCreateException::new);
  }

  private AuthUser userFromJwt(Jwt jwt) {
    return new AuthUser(
        blankToNull(jwt.getClaimAsString("sub")),
        authUserEmailMapper.resolveEmail(jwt),
        blankToNull(jwt.getClaimAsString("given_name")),
        blankToNull(jwt.getClaimAsString("family_name")));
  }

  private static String blankToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
