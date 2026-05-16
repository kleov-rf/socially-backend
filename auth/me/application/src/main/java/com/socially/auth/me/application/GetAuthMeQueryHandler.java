package com.socially.auth.me.application;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserEmailMapper;
import com.socially.auth.me.application.exception.MeUserNotFoundException;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.output.AuthMeQueryResult;
import com.socially.auth.me.application.port.left.GetAuthMeUseCase;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
public final class GetAuthMeQueryHandler implements GetAuthMeUseCase {

  private final FindUserByEmailUseCase findUserByEmailUseCase;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  private final AuthUserEmailMapper authUserEmailMapper;

  @Override
  public AuthMeQueryResult execute(Principal principal) {
    if (!(principal instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
      throw new UnauthenticatedRequestException();
    }
    AuthUser authUser = userFromJwt(jwtAuthenticationToken.getToken());
    User user =
        findUserByEmailUseCase
            .execute(new FindUserByEmailQuery(authUser.email()))
            .orElseThrow(MeUserNotFoundException::new);
    Optional<Donor> donor =
        findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(user.id().value().toString()));
    return new AuthMeQueryResult(user, donor);
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
