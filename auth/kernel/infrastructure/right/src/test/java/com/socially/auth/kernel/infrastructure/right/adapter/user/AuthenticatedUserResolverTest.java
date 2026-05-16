package com.socially.auth.kernel.infrastructure.right.adapter.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.MissingOidcIdentityClaimsException;
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
      new AuthUser("https://idp.example", "sub-1", "user@example.com", "Jane", "Doe");
  private static final String CREATE_USER_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final CreateUserCommand CREATE_USER_COMMAND =
      new CreateUserCommand(CREATE_USER_ID, "user@example.com", "Jane", "Doe");
  private static final User EXISTING_USER =
      User.create(
          Id.from("550e8400-e29b-41d4-a716-446655440000"),
          Email.from("user@example.com"),
          "Jane",
          "Doe",
          Instant.parse("2024-06-01T12:00:00Z"));
  private static final User REFETCHED_USER =
      User.create(
          Id.from(CREATE_USER_ID),
          Email.from("user@example.com"),
          "Jane",
          "Doe",
          Instant.parse("2024-06-01T12:00:00Z"));

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
    AuthUser invalidUser = new AuthUser(null, "sub-1", "user@example.com", "Jane", "Doe");
    org.mockito.Mockito.doThrow(new MissingOidcIdentityClaimsException())
        .when(authUserClaimsValidator)
        .requireIssuerAndSubject(invalidUser);

    assertThrows(MissingOidcIdentityClaimsException.class, () -> resolver.resolve(invalidUser));
  }

  @Test
  void resolve_should_call_find_by_federated_identity_with_auth_user_issuer_and_subject() {
    User updatedUser =
        User.create(
            EXISTING_USER.id(),
            Email.from("updated@example.com"),
            "Janet",
            "Doe",
            EXISTING_USER.createdAt());
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(updatedUser));

    resolver.resolve(AUTH_USER);

    verify(findUserByFederatedIdentityUseCase, times(2)).execute(FEDERATED_QUERY);
  }

  @Test
  void resolve_should_call_update_user_profile_when_federated_identity_exists() {
    User updatedUser =
        User.create(
            EXISTING_USER.id(),
            Email.from("updated@example.com"),
            "Janet",
            "Doe",
            EXISTING_USER.createdAt());
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(updatedUser));

    resolver.resolve(AUTH_USER);

    verify(updateUserProfileUseCase)
        .execute(
            new UpdateUserProfileCommand(
                EXISTING_USER.id().value().toString(),
                "https://idp.example",
                "sub-1",
                "user@example.com",
                "Jane",
                "Doe"));
  }

  @Test
  void resolve_should_return_refetched_user_when_federated_identity_exists() {
    User updatedUser =
        User.create(
            EXISTING_USER.id(),
            Email.from("updated@example.com"),
            "Janet",
            "Doe",
            EXISTING_USER.createdAt());
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(updatedUser));

    User result = resolver.resolve(AUTH_USER);

    assertEquals(updatedUser, result);
  }

  @Test
  void resolve_should_not_call_create_user_when_federated_identity_exists() {
    User updatedUser =
        User.create(
            EXISTING_USER.id(),
            Email.from("updated@example.com"),
            "Janet",
            "Doe",
            EXISTING_USER.createdAt());
    when(findUserByFederatedIdentityUseCase.execute(FEDERATED_QUERY))
        .thenReturn(Optional.of(EXISTING_USER))
        .thenReturn(Optional.of(updatedUser));

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
}
