package com.socially.auth.kernel.infrastructure.right.adapter.user;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.infrastructure.right.adapter.user.mapper.AuthUserToCreateUserCommandMapper;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.federatedidentity.link.application.input.LinkFederatedIdentityCommand;
import com.socially.user.federatedidentity.link.application.port.left.LinkFederatedIdentityUseCase;
import com.socially.user.findbyfederatedidentity.application.input.FindUserByFederatedIdentityQuery;
import com.socially.user.findbyfederatedidentity.application.port.left.FindUserByFederatedIdentityUseCase;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.updateprofile.application.input.UpdateUserProfileCommand;
import com.socially.user.updateprofile.application.port.left.UpdateUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class AuthenticatedUserResolver {

  private final AuthUserClaimsValidator authUserClaimsValidator;
  private final FindUserByFederatedIdentityUseCase findUserByFederatedIdentityUseCase;
  private final UpdateUserProfileUseCase updateUserProfileUseCase;
  private final CreateUserUseCase createUserUseCase;
  private final LinkFederatedIdentityUseCase linkFederatedIdentityUseCase;
  private final AuthUserToCreateUserCommandMapper authUserToCreateUserCommandMapper;

  public User resolve(AuthUser authUser) {
    authUserClaimsValidator.requireIssuerAndSubject(authUser);

    return findUserByFederatedIdentityUseCase
        .execute(new FindUserByFederatedIdentityQuery(authUser.issuer(), authUser.subject()))
        .map(existing -> updateExistingUser(existing, authUser))
        .orElseGet(() -> createAndLinkUser(authUser));
  }

  private User updateExistingUser(User existing, AuthUser authUser) {
    return updateUserProfileUseCase.execute(
        new UpdateUserProfileCommand(
            existing.id().value().toString(),
            authUser.issuer(),
            authUser.subject(),
            authUser.email(),
            authUser.givenName(),
            authUser.familyName()));
  }

  private User createAndLinkUser(AuthUser authUser) {
    User createdUser = createUserUseCase.execute(authUserToCreateUserCommandMapper.toCommand(authUser));
    linkFederatedIdentityUseCase.execute(
        new LinkFederatedIdentityCommand(
            createdUser.id().value().toString(),
            authUser.issuer(),
            authUser.subject(),
            authUser.email()));
    return createdUser;
  }
}
