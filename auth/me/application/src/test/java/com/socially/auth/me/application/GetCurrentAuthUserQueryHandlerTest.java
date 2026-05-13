package com.socially.auth.me.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.infrastructure.right.adapter.user.mapper.AuthUserToCreateUserCommandMapper;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.domain.valueobject.Id;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class GetCurrentAuthUserQueryHandlerTest {

  private static final Instant ISSUED_AT = Instant.parse("2024-01-01T00:00:00Z");
  private static final Instant EXPIRES_AT = Instant.parse("2024-01-01T01:00:00Z");
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final CreateUserCommand CREATE_USER_COMMAND =
      new CreateUserCommand("id-test@example.com", "Jane", "Doe");

  @Mock private AuthUserToCreateUserCommandMapper authUserToCreateUserCommandMapper;
  @Mock private CreateUserUseCase createUserUseCase;

  @InjectMocks private GetCurrentAuthUserQueryHandler handler;

  @Test
  void should_call_command_mapper_with_correct_auth_user() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    User createdUser =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("id-test@example.com"),
            "Jane",
            "Doe",
            CREATED_AT);
    when(authUserToCreateUserCommandMapper.toCommand(
            new AuthUser("sub-99", "id-test@example.com", "Jane Doe")))
        .thenReturn(CREATE_USER_COMMAND);
    when(createUserUseCase.execute(CREATE_USER_COMMAND)).thenReturn(createdUser);

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(authUserToCreateUserCommandMapper)
        .toCommand(
            argThat(
                authUser ->
                    "sub-99".equals(authUser.id())
                        && "id-test@example.com".equals(authUser.email())
                        && "Jane Doe".equals(authUser.name())));
  }

  @Test
  void should_call_create_use_case_with_mapped_command() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    User createdUser =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("id-test@example.com"),
            "Jane",
            "Doe",
            CREATED_AT);
    when(authUserToCreateUserCommandMapper.toCommand(
            new AuthUser("sub-99", "id-test@example.com", "Jane Doe")))
        .thenReturn(CREATE_USER_COMMAND);
    when(createUserUseCase.execute(CREATE_USER_COMMAND)).thenReturn(createdUser);

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(createUserUseCase).execute(CREATE_USER_COMMAND);
  }

  @Test
  void should_return_result_from_create_use_case() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    User createdUser =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("id-test@example.com"),
            "Jane",
            "Doe",
            CREATED_AT);
    when(authUserToCreateUserCommandMapper.toCommand(
            new AuthUser("sub-99", "id-test@example.com", "Jane Doe")))
        .thenReturn(CREATE_USER_COMMAND);
    when(createUserUseCase.execute(CREATE_USER_COMMAND)).thenReturn(createdUser);

    User result = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals(createdUser, result);
  }

  @Test
  void should_throw_exception_if_received_principal_is_not_auth_token() {
    UnauthenticatedRequestException exception =
        assertThrows(
            UnauthenticatedRequestException.class, () -> handler.execute(() -> "anonymous"));

    assertEquals("Unauthenticated request", exception.getMessage());
  }

  private static Map<String, Object> baseClaims(String sub, String email) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", sub);
    claims.put("email", email);
    claims.put("given_name", "Jane");
    claims.put("family_name", "Doe");
    return claims;
  }

  private static Jwt jwtWithClaims(Map<String, Object> claims) {
    return new Jwt("token-value", ISSUED_AT, EXPIRES_AT, Map.of("alg", "none"), claims);
  }
}
