package com.socially.auth.refresh.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.exception.AuthenticatedUserNotFoundException;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.kernel.infrastructure.right.adapter.oauth.mapper.AuthResultMapper;
import com.socially.auth.kernel.infrastructure.right.adapter.user.AuthenticatedUserResolver;
import com.socially.auth.refresh.application.exception.InvalidRefreshedIdTokenException;
import com.socially.auth.refresh.application.exception.MissingRefreshSessionException;
import com.socially.auth.refresh.application.mapper.RefreshCookieInstructionsMapper;
import com.socially.auth.refresh.domain.port.right.RefreshTokenExchangeOAuthClient;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshSessionCommandHandlerTest {

  @Mock private RefreshTokenExchangeOAuthClient refreshTokenExchangeOAuthClient;
  @Mock private AuthProperties authProperties;
  @Mock private RefreshCookieInstructionsMapper refreshCookieInstructionsMapper;
  @Mock private AuthResultMapper authResultMapper;
  @Mock private AuthenticatedUserResolver authenticatedUserResolver;

  @InjectMocks private RefreshSessionCommandHandler handler;

  private static final AuthUser AUTH_USER =
      new AuthUser("https://idp.example", "auth-id", "e@x.com", "Jane", "Doe");
  private static final OAuthTokenResponse TOKEN_RESPONSE =
      new OAuthTokenResponse("access", "id-token", "refresh-2", "Bearer", 3600L);
  private static final AuthResult AUTH_RESULT =
      new AuthResult("access", "Bearer", 3600L, AUTH_USER);
  private static final User RESOLVED_USER =
      User.create(
          Id.from("550e8400-e29b-41d4-a716-446655440111"),
          Email.from("e@x.com"),
          "Jane",
          "Doe",
          Instant.parse("2024-06-01T12:00:00Z"));
  private static final CookieInstruction COOKIE_INSTRUCTION =
      new CookieInstruction("socially_refresh_token", "refresh-2", 86_400L);

  @Test
  void execute_should_return_exception_when_received_refresh_token_is_not_present() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");

    MissingRefreshSessionException exception =
        assertThrows(MissingRefreshSessionException.class, () -> handler.execute(Map.of()));

    assertEquals("Missing refresh session", exception.getMessage());
  }

  @Test
  void execute_should_call_oauth_client_with_received_refresh_token() {
    stubSuccessfulExecution();

    handler.execute(Map.of("socially_refresh_token", "refresh-2"));

    verify(refreshTokenExchangeOAuthClient).exchangeRefreshToken("refresh-2");
  }

  @Test
  void execute_should_delegate_cookie_instruction_mapping() {
    stubSuccessfulExecution();

    handler.execute(Map.of("socially_refresh_token", "refresh-2"));

    verify(refreshCookieInstructionsMapper).toCookieInstruction(TOKEN_RESPONSE);
  }

  @Test
  void execute_should_delegate_auth_result_mapping() {
    stubSuccessfulExecution();

    handler.execute(Map.of("socially_refresh_token", "refresh-2"));

    verify(authResultMapper).toAuthResult(TOKEN_RESPONSE);
  }

  @Test
  void execute_should_call_authenticated_user_resolver_with_auth_user_from_result() {
    stubSuccessfulExecution();

    handler.execute(Map.of("socially_refresh_token", "refresh-2"));

    verify(authenticatedUserResolver).resolveExisting(AUTH_USER);
  }

  @Test
  void execute_should_throw_authenticated_user_not_found_when_resolver_reports_missing_user() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-2"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE))
        .thenReturn(COOKIE_INSTRUCTION);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);
    when(authenticatedUserResolver.resolveExisting(AUTH_USER))
        .thenThrow(new AuthenticatedUserNotFoundException());

    AuthenticatedUserNotFoundException exception =
        assertThrows(
            AuthenticatedUserNotFoundException.class,
            () -> handler.execute(Map.of("socially_refresh_token", "refresh-2")));

    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void execute_should_return_result_with_auth_result_user_and_cookie_instruction() {
    stubSuccessfulExecution();

    var result = handler.execute(Map.of("socially_refresh_token", "refresh-2"));

    assertEquals(AUTH_RESULT, result.authResult());
    assertEquals(RESOLVED_USER, result.user());
    assertEquals(COOKIE_INSTRUCTION, result.cookieInstruction());
  }

  @Test
  void execute_should_throw_invalid_refreshed_id_token_when_auth_result_mapper_fails() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-2"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE))
        .thenReturn(COOKIE_INSTRUCTION);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE))
        .thenThrow(new IllegalArgumentException("invalid id token"));

    InvalidRefreshedIdTokenException exception =
        assertThrows(
            InvalidRefreshedIdTokenException.class,
            () -> handler.execute(Map.of("socially_refresh_token", "refresh-2")));

    assertEquals("invalid id token", exception.getMessage());
  }

  private void stubSuccessfulExecution() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-2"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE))
        .thenReturn(COOKIE_INSTRUCTION);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);
    when(authenticatedUserResolver.resolveExisting(AUTH_USER)).thenReturn(RESOLVED_USER);
  }
}
