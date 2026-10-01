package com.socially.auth.kernel.infrastructure.right.adapter.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.AuthBadRequestException;
import com.socially.auth.kernel.domain.exception.AuthUnauthorizedException;
import com.socially.auth.kernel.infrastructure.right.adapter.user.mapper.AuthUserToCreateUserCommandMapper;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.federatedidentity.link.application.input.LinkFederatedIdentityCommand;
import com.socially.user.federatedidentity.link.application.port.left.LinkFederatedIdentityUseCase;
import com.socially.user.findbyfederatedidentity.application.input.FindUserByFederatedIdentityQuery;
import com.socially.user.findbyfederatedidentity.application.port.left.FindUserByFederatedIdentityUseCase;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.updateprofile.application.input.UpdateUserProfileCommand;
import com.socially.user.updateprofile.application.port.left.UpdateUserProfileUseCase;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserResolverTest {

  private static final FindUserByFederatedIdentityQuery FEDERATED_QUERY =
      new FindUserByFederatedIdentityQuery("https://idp.example", "sub-1");
  private static final AuthUser AUTH_USER =
      AuthUser.create("https://idp.example", "sub-1", "user@example.com")
          .withGivenName(Optional.of("Jane"))
          .withFamilyName(Optional.of("Doe"));
  private static final String CREATE_USER_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final CreateUserCommand CREATE_USER_COMMAND =
      CreateUserCommand.create(CREATE_USER_ID, "user@example.com")
          .withGivenName(Optional.of("Jane"))
          .withFamilyName(Optional.of("Doe"));
  private static final User EXISTING_USER =
      User.create(
              Id.from("550e8400-e29b-41d4-a716-446655440000"),
              Email.from("user@example.com"),
              Instant.parse("2024-06-01T12:00:00Z"))
          .withGivenName(Optional.of("Jane"))
          .withFamilyName(Optional.of("Doe"));
  private static final User REFETCHED_USER =
      User.create(
              Id.from(CREATE_USER_ID),
              Email.from("user@example.com"),
              Instant.parse("2024-06-01T12:00:00Z"))
          .withGivenName(Optional.of("Jane"))
          .withFamilyName(Optional.of("Doe"));
  private static final User UPDATED_USER =
      User.create(EXISTING_USER.id(), Email.from("updated@example.com"), EXISTING_USER.createdAt())
          .withGivenName(Optional.of("Janet"))
          .withFamilyName(Optional.of("Doe"));

  @Mock private AuthUserClaimsValidator authUserClaimsValidator;
  @Mock private FindUserByFederatedIdentityUseCase findUserByFederatedIdentityUseCase;
  @Mock private UpdateUserProfileUseCase updateUserProfileUseCase;
  @Mock private CreateUserUseCase createUserUseCase;
  @Mock private LinkFederatedIdentityUseCase linkFederatedIdentityUseCase;
  @Mock private AuthUserToCreateUserCommandMapper authUserToCreateUserCommandMapper;

  @InjectMocks private AuthenticatedUserResolver resolver;

  @Test
  void resolve_should_call_auth_user_claims_validator_with_received_auth_user() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(REFETCHED_USER));
    when(authUserToCreateUserCommandMapper.toCommand(AUTH_USER)).thenReturn(CREATE_USER_COMMAND);

    resolver.resolve(AUTH_USER);

    verify(authUserClaimsValidator).requireIssuerAndSubject(AUTH_USER);
  }

  @Test
  void resolve_should_throw_when_auth_user_claims_validator_throws() {
    AuthUser invalidUser =
        AuthUser.create(null, "sub-1", "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    org.mockito.Mockito.doThrow(
            new AuthBadRequestException("OIDC issuer and subject claims are required"))
        .when(authUserClaimsValidator)
        .requireIssuerAndSubject(invalidUser);

    AuthBadRequestException exception =
        assertThrows(AuthBadRequestException.class, () -> resolver.resolve(invalidUser));
    assertEquals("OIDC issuer and subject claims are required", exception.getMessage());
  }

  @Test
  void resolve_should_call_find_by_federated_identity_with_auth_user_issuer_and_subject() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(UPDATED_USER));

    resolver.resolve(AUTH_USER);

    verify(findUserByFederatedIdentityUseCase, times(2)).execute(FEDERATED_QUERY);
  }

  @Test
  void resolve_should_call_update_user_profile_when_federated_identity_exists() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(UPDATED_USER));

    resolver.resolve(AUTH_USER);

    verify(updateUserProfileUseCase)
        .execute(
            UpdateUserProfileCommand.create(
                    EXISTING_USER.id().value().toString(), "https://idp.example", "sub-1")
                .withEmail(Optional.of("user@example.com"))
                .withGivenName(Optional.of("Jane"))
                .withFamilyName(Optional.of("Doe")));
  }

  @Test
  void resolve_should_return_refetched_user_when_federated_identity_exists() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(UPDATED_USER));

    User result = resolver.resolve(AUTH_USER);

    assertEquals(UPDATED_USER, result);
  }

  @Test
  void resolve_should_not_call_create_user_when_federated_identity_exists() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(UPDATED_USER));

    resolver.resolve(AUTH_USER);

    verify(createUserUseCase, never()).execute(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void resolve_should_call_create_user_when_federated_identity_missing() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(REFETCHED_USER));
    when(authUserToCreateUserCommandMapper.toCommand(AUTH_USER)).thenReturn(CREATE_USER_COMMAND);

    resolver.resolve(AUTH_USER);

    verify(createUserUseCase).execute(CREATE_USER_COMMAND);
  }

  @Test
  void resolve_should_call_link_federated_identity_with_user_id_from_create_command() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(REFETCHED_USER));
    when(authUserToCreateUserCommandMapper.toCommand(AUTH_USER)).thenReturn(CREATE_USER_COMMAND);

    resolver.resolve(AUTH_USER);

    verify(linkFederatedIdentityUseCase)
        .execute(
            new LinkFederatedIdentityCommand(
                CREATE_USER_ID, "https://idp.example", "sub-1", "user@example.com"));
  }

  @Test
  void resolve_should_return_refetched_user_when_federated_identity_missing() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(REFETCHED_USER));
    when(authUserToCreateUserCommandMapper.toCommand(AUTH_USER)).thenReturn(CREATE_USER_COMMAND);

    User result = resolver.resolve(AUTH_USER);

    assertEquals(REFETCHED_USER, result);
  }

  @Test
  void resolveExisting_should_throw_authenticated_user_not_found_when_federated_identity_missing() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY)).thenReturn(Optional.empty());

    AuthUnauthorizedException exception =
        assertThrows(AuthUnauthorizedException.class, () -> resolver.resolveExisting(AUTH_USER));

    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void resolveExisting_should_call_update_user_profile_when_federated_identity_exists() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(UPDATED_USER));

    resolver.resolveExisting(AUTH_USER);

    verify(updateUserProfileUseCase)
        .execute(
            UpdateUserProfileCommand.create(
                    EXISTING_USER.id().value().toString(), "https://idp.example", "sub-1")
                .withEmail(Optional.of("user@example.com"))
                .withGivenName(Optional.of("Jane"))
                .withFamilyName(Optional.of("Doe")));
  }

  @Test
  void resolveExisting_should_never_call_create_user() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(UPDATED_USER));

    resolver.resolveExisting(AUTH_USER);

    verify(createUserUseCase, never()).execute(org.mockito.ArgumentMatchers.any());
    verify(linkFederatedIdentityUseCase, never()).execute(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void resolveExisting_should_return_refetched_user_after_profile_update() {
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(UPDATED_USER));

    User result = resolver.resolveExisting(AUTH_USER);

    assertEquals(UPDATED_USER, result);
  }
}
