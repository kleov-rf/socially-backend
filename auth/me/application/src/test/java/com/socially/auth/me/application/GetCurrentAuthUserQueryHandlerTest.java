package com.socially.auth.me.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.UserNotFoundAfterCreateException;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthUserEmailMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.mapper.AuthUserToCreateUserCommandMapper;
import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
  private static final AuthUser EXPECTED_AUTH_USER =
      new AuthUser("sub-99", "id-test@example.com", "Jane", "Doe");

  @Mock private CreateUserUseCase createUserUseCase;
  @Mock private FindUserByEmailUseCase findUserByEmailUseCase;
  @Mock private AuthUserToCreateUserCommandMapper authUserToCreateUserCommandMapper;
  @Mock private AuthUserEmailMapper authUserEmailMapper;

  @InjectMocks private GetCurrentAuthUserQueryHandler handler;

  @Test
  void execute_should_call_auth_user_email_mapper_with_current_jwt() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    User foundUser = foundUser();
    when(authUserEmailMapper.resolveEmail(same(jwt))).thenReturn("id-test@example.com");
    when(authUserToCreateUserCommandMapper.toCommand(eq(EXPECTED_AUTH_USER)))
        .thenReturn(CREATE_USER_COMMAND);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email())))
        .thenReturn(Optional.of(foundUser));

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(authUserEmailMapper).resolveEmail(same(jwt));
  }

  @Test
  void execute_should_call_command_mapper_with_built_auth_user() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    User foundUser = foundUser();
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("id-test@example.com");
    when(authUserToCreateUserCommandMapper.toCommand(eq(EXPECTED_AUTH_USER)))
        .thenReturn(CREATE_USER_COMMAND);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email())))
        .thenReturn(Optional.of(foundUser));

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(authUserToCreateUserCommandMapper).toCommand(eq(EXPECTED_AUTH_USER));
  }

  @Test
  void execute_should_call_create_use_case_with_mapped_command() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    User foundUser = foundUser();
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("id-test@example.com");
    when(authUserToCreateUserCommandMapper.toCommand(eq(EXPECTED_AUTH_USER)))
        .thenReturn(CREATE_USER_COMMAND);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email())))
        .thenReturn(Optional.of(foundUser));

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(createUserUseCase).execute(CREATE_USER_COMMAND);
  }

  @Test
  void execute_should_call_find_by_email_use_case_with_mapped_email() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    User foundUser = foundUser();
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("id-test@example.com");
    when(authUserToCreateUserCommandMapper.toCommand(eq(EXPECTED_AUTH_USER)))
        .thenReturn(CREATE_USER_COMMAND);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email())))
        .thenReturn(Optional.of(foundUser));

    handler.execute(new JwtAuthenticationToken(jwt));

    verify(findUserByEmailUseCase).execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email()));
  }

  @Test
  void execute_should_return_result_from_find_by_email_use_case() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    User foundUser = foundUser();
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("id-test@example.com");
    when(authUserToCreateUserCommandMapper.toCommand(eq(EXPECTED_AUTH_USER)))
        .thenReturn(CREATE_USER_COMMAND);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email())))
        .thenReturn(Optional.of(foundUser));

    User result = handler.execute(new JwtAuthenticationToken(jwt));

    assertEquals(foundUser, result);
  }

  @Test
  void execute_should_throw_exception_when_user_not_found_after_create() {
    Jwt jwt = jwtWithClaims(baseClaims("sub-99", "id-test@example.com"));
    when(authUserEmailMapper.resolveEmail(any(Jwt.class))).thenReturn("id-test@example.com");
    when(authUserToCreateUserCommandMapper.toCommand(eq(EXPECTED_AUTH_USER)))
        .thenReturn(CREATE_USER_COMMAND);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email())))
        .thenReturn(Optional.empty());

    UserNotFoundAfterCreateException exception =
        assertThrows(
            UserNotFoundAfterCreateException.class,
            () -> handler.execute(new JwtAuthenticationToken(jwt)));

    assertEquals("User not found after create", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_if_received_principal_is_not_auth_token() {
    UnauthenticatedRequestException exception =
        assertThrows(
            UnauthenticatedRequestException.class, () -> handler.execute(() -> "anonymous"));

    assertEquals("Unauthenticated request", exception.getMessage());
  }

  private static User foundUser() {
    return User.create(
        Id.from("550e8400-e29b-41d4-a716-446655440000"),
        Email.from("id-test@example.com"),
        "Jane",
        "Doe",
        CREATED_AT);
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
