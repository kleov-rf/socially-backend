package com.socially.auth.callback.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.callback.application.exception.InvalidOAuthStateException;
import com.socially.auth.callback.application.exception.MissingTemporaryOAuthCookiesException;
import com.socially.auth.callback.application.mapper.CallbackCookieInstructionsMapper;
import com.socially.auth.callback.domain.port.right.AuthorizationCodeExchangeOAuthClient;
import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.kernel.domain.exception.UserNotFoundAfterCreateException;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthResultMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.mapper.AuthUserToCreateUserCommandMapper;
import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.create.application.port.left.CreateUserUseCase;
import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.findbyemail.application.port.left.FindUserByEmailUseCase;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.domain.valueobject.Id;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompleteOAuthCallbackCommandHandlerTest {

  @Mock private AuthorizationCodeExchangeOAuthClient authorizationCodeExchangeOAuthClient;
  @Mock private AuthProperties authProperties;
  @Mock private CallbackCookieInstructionsMapper callbackCookieInstructionsMapper;
  @Mock private AuthResultMapper authResultMapper;
  @Mock private AuthUserToCreateUserCommandMapper authUserToCreateUserCommandMapper;
  @Mock private CreateUserUseCase createUserUseCase;
  @Mock private FindUserByEmailUseCase findUserByEmailUseCase;

  @InjectMocks private CompleteOAuthCallbackCommandHandler sut;

  private static final String CODE = "code-1";
  private static final String STATE = "state-1";
  private static final String CODE_VERIFIER = "verifier-1";
  private static final String ID_TOKEN = "id-token-1";
  private static final String STATE_COOKIE_NAME = "socially_oauth_state";
  private static final String PKCE_COOKIE_NAME = "socially_oauth_pkce";
  private static final AuthUser USER = new AuthUser("user-id-1", "user@example.com", "John", "Doe");
  private static final CreateUserCommand CREATE_USER_COMMAND =
      new CreateUserCommand("user@example.com", "John", "Doe");
  private static final User CREATED_USER =
      User.create(
          Id.from("550e8400-e29b-41d4-a716-446655440000"),
          Email.from("user@example.com"),
          "John",
          "Doe",
          Instant.parse("2024-06-01T12:00:00Z"));
  private static final OAuthTokenResponse TOKEN_RESPONSE =
      new OAuthTokenResponse("access-token-1", ID_TOKEN, "refresh-1", "Bearer", 3600L);
  private static final AuthResult AUTH_RESULT =
      new AuthResult("access-token-1", "Bearer", 3600L, USER);
  private static final List<CookieInstruction> COOKIE_INSTRUCTIONS =
      List.of(new CookieInstruction("state", "", 0L), new CookieInstruction("pkce", "", 0L));

  @Test
  void execute_should_throw_exception_when_state_cookie_is_not_present() {
    when(authProperties.stateCookieName()).thenReturn(STATE_COOKIE_NAME);
    when(authProperties.pkceCookieName()).thenReturn(PKCE_COOKIE_NAME);

    MissingTemporaryOAuthCookiesException exception =
        assertThrows(
            MissingTemporaryOAuthCookiesException.class,
            () -> sut.execute(CODE, STATE, Map.of(PKCE_COOKIE_NAME, CODE_VERIFIER)));

    assertEquals("Missing temporary OAuth cookies", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_code_verifier_cookie_is_not_present() {
    when(authProperties.stateCookieName()).thenReturn(STATE_COOKIE_NAME);
    when(authProperties.pkceCookieName()).thenReturn(PKCE_COOKIE_NAME);

    MissingTemporaryOAuthCookiesException exception =
        assertThrows(
            MissingTemporaryOAuthCookiesException.class,
            () -> sut.execute(CODE, STATE, Map.of(STATE_COOKIE_NAME, STATE)));

    assertEquals("Missing temporary OAuth cookies", exception.getMessage());
  }

  @Test
  void execute_should_throw_exception_when_state_cookie_not_matches_received_state() {
    when(authProperties.stateCookieName()).thenReturn(STATE_COOKIE_NAME);
    when(authProperties.pkceCookieName()).thenReturn(PKCE_COOKIE_NAME);

    InvalidOAuthStateException exception =
        assertThrows(
            InvalidOAuthStateException.class,
            () ->
                sut.execute(
                    CODE,
                    "different-state",
                    Map.of(STATE_COOKIE_NAME, STATE, PKCE_COOKIE_NAME, CODE_VERIFIER)));

    assertEquals("Invalid OAuth state parameter", exception.getMessage());
  }

  @Test
  void execute_should_call_oauth_client_with_received_code_and_code_verifier_cookie() {
    stubSuccessfulExecution();

    sut.execute(CODE, STATE, validRequestCookies());

    verify(authorizationCodeExchangeOAuthClient).exchangeAuthorizationCode(CODE, CODE_VERIFIER);
  }

  @Test
  void execute_should_delegate_cookie_header_mapping() {
    stubSuccessfulExecution();

    sut.execute(CODE, STATE, validRequestCookies());

    verify(callbackCookieInstructionsMapper).toCookieInstructions(TOKEN_RESPONSE);
  }

  @Test
  void execute_should_delegate_auth_result_mapping() {
    stubSuccessfulExecution();

    sut.execute(CODE, STATE, validRequestCookies());

    verify(authResultMapper).toAuthResult(TOKEN_RESPONSE);
  }

  @Test
  void execute_should_return_outcome_with_mapped_values() {
    stubSuccessfulExecution();

    var outcome = sut.execute(CODE, STATE, validRequestCookies());

    assertEquals(AUTH_RESULT, outcome.authResult());
    assertEquals(CREATED_USER, outcome.user());
    assertEquals(COOKIE_INSTRUCTIONS, outcome.cookieInstructions());
  }

  @Test
  void execute_should_call_create_use_case_with_mapped_command() {
    stubSuccessfulExecution();

    sut.execute(CODE, STATE, validRequestCookies());

    verify(createUserUseCase).execute(CREATE_USER_COMMAND);
  }

  @Test
  void execute_should_call_find_by_email_use_case_with_command_email() {
    stubSuccessfulExecution();

    sut.execute(CODE, STATE, validRequestCookies());

    verify(findUserByEmailUseCase).execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email()));
  }

  @Test
  void execute_should_throw_exception_when_created_user_cannot_be_found() {
    when(authProperties.stateCookieName()).thenReturn(STATE_COOKIE_NAME);
    when(authProperties.pkceCookieName()).thenReturn(PKCE_COOKIE_NAME);
    when(authorizationCodeExchangeOAuthClient.exchangeAuthorizationCode(CODE, CODE_VERIFIER))
        .thenReturn(TOKEN_RESPONSE);
    when(callbackCookieInstructionsMapper.toCookieInstructions(TOKEN_RESPONSE))
        .thenReturn(COOKIE_INSTRUCTIONS);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);
    when(authUserToCreateUserCommandMapper.toCommand(USER)).thenReturn(CREATE_USER_COMMAND);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email())))
        .thenReturn(Optional.empty());

    UserNotFoundAfterCreateException exception =
        assertThrows(
            UserNotFoundAfterCreateException.class,
            () -> sut.execute(CODE, STATE, validRequestCookies()));

    assertEquals("User not found after create", exception.getMessage());
  }

  @Test
  void execute_should_propagate_exception_when_auth_result_mapper_throws() {
    when(authProperties.stateCookieName()).thenReturn(STATE_COOKIE_NAME);
    when(authProperties.pkceCookieName()).thenReturn(PKCE_COOKIE_NAME);
    when(authorizationCodeExchangeOAuthClient.exchangeAuthorizationCode(CODE, CODE_VERIFIER))
        .thenReturn(TOKEN_RESPONSE);
    when(callbackCookieInstructionsMapper.toCookieInstructions(TOKEN_RESPONSE))
        .thenReturn(COOKIE_INSTRUCTIONS);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE))
        .thenThrow(new IllegalArgumentException("invalid id token"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> sut.execute(CODE, STATE, validRequestCookies()));

    assertEquals("invalid id token", exception.getMessage());
  }

  private void stubSuccessfulExecution() {
    when(authProperties.stateCookieName()).thenReturn(STATE_COOKIE_NAME);
    when(authProperties.pkceCookieName()).thenReturn(PKCE_COOKIE_NAME);
    when(authorizationCodeExchangeOAuthClient.exchangeAuthorizationCode(CODE, CODE_VERIFIER))
        .thenReturn(TOKEN_RESPONSE);
    when(callbackCookieInstructionsMapper.toCookieInstructions(TOKEN_RESPONSE))
        .thenReturn(COOKIE_INSTRUCTIONS);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);
    when(authUserToCreateUserCommandMapper.toCommand(USER)).thenReturn(CREATE_USER_COMMAND);
    when(findUserByEmailUseCase.execute(new FindUserByEmailQuery(CREATE_USER_COMMAND.email())))
        .thenReturn(Optional.of(CREATED_USER));
  }

  private Map<String, String> validRequestCookies() {
    return Map.of(STATE_COOKIE_NAME, STATE, PKCE_COOKIE_NAME, CODE_VERIFIER);
  }
}
