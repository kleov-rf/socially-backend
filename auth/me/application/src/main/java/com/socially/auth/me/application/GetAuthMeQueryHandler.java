package com.socially.auth.me.application;

import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserFromJwtMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.AuthUserClaimsValidator;
import com.socially.auth.me.application.exception.MeUserNotFoundException;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.output.AuthMeQueryResult;
import com.socially.auth.me.application.port.left.GetAuthMeUseCase;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.findbyfederatedidentity.application.input.FindUserByFederatedIdentityQuery;
import com.socially.user.findbyfederatedidentity.application.port.left.FindUserByFederatedIdentityUseCase;
import com.socially.user.kernel.domain.entity.User;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public final class GetAuthMeQueryHandler implements GetAuthMeUseCase {

  private final FindUserByFederatedIdentityUseCase findUserByFederatedIdentityUseCase;
  private final FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  private final AuthUserFromJwtMapper authUserFromJwtMapper;
  private final AuthUserClaimsValidator authUserClaimsValidator;

  @Override
  public AuthMeQueryResult execute(Principal principal) {
    if (!(principal instanceof JwtAuthenticationToken jwtAuthenticationToken)) {
      throw new UnauthenticatedRequestException();
    }
    var authUser = authUserFromJwtMapper.fromJwt(jwtAuthenticationToken.getToken());
    authUserClaimsValidator.requireIssuerAndSubject(authUser);
    User user =
        findUserByFederatedIdentityUseCase
            .execute(new FindUserByFederatedIdentityQuery(authUser.issuer(), authUser.subject()))
            .orElseThrow(MeUserNotFoundException::new);
    Optional<Donor> donor =
        findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(user.id().value().toString()));
    return new AuthMeQueryResult(user, donor);
  }
}
