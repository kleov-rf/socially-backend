package com.socially.auth.refresh.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthResultMapper;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.refresh.application.exception.InvalidRefreshedIdTokenException;
import com.socially.auth.refresh.application.exception.MissingRefreshSessionException;
import com.socially.auth.refresh.application.mapper.RefreshCookieInstructionsMapper;
import com.socially.auth.refresh.domain.port.right.RefreshTokenExchangeOAuthClient;
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

  @InjectMocks private RefreshSessionCommandHandler handler;

  private static final OAuthTokenResponse TOKEN_RESPONSE =
      new OAuthTokenResponse("access", "id-token", "refresh-2", "Bearer", 3600L);
  private static final AuthResult AUTH_RESULT = new AuthResult("access", "Bearer", 3600L, null);
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
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-1"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE)).thenReturn(null);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);

    handler.execute(Map.of("socially_refresh_token", "refresh-1"));

    verify(refreshTokenExchangeOAuthClient).exchangeRefreshToken("refresh-1");
  }

  @Test
  void execute_should_call_cookie_instructions_mapper_with_retrieved_token_response() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-1"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE)).thenReturn(null);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);

    handler.execute(Map.of("socially_refresh_token", "refresh-1"));

    verify(refreshCookieInstructionsMapper).toCookieInstruction(TOKEN_RESPONSE);
  }

  @Test
  void execute_should_call_auth_result_mapper_with_retrieved_token_response() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-1"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE)).thenReturn(null);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);

    handler.execute(Map.of("socially_refresh_token", "refresh-1"));

    verify(authResultMapper).toAuthResult(TOKEN_RESPONSE);
  }

  @Test
  void execute_should_throw_exception_when_result_mapper_throws_exception() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-1"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE))
        .thenReturn(COOKIE_INSTRUCTION);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE))
        .thenThrow(new IllegalArgumentException("invalid id token"));

    InvalidRefreshedIdTokenException exception =
        assertThrows(
            InvalidRefreshedIdTokenException.class,
            () -> handler.execute(Map.of("socially_refresh_token", "refresh-1")));

    assertEquals("invalid id token", exception.getMessage());
  }

  @Test
  void execute_should_return_result_with_mapped_refresh_result() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-1"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE)).thenReturn(null);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);

    var result = handler.execute(Map.of("socially_refresh_token", "refresh-1"));

    assertEquals(AUTH_RESULT, result.authResult());
  }

  @Test
  void execute_should_return_result_with_mapped_cookies() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(refreshTokenExchangeOAuthClient.exchangeRefreshToken("refresh-1"))
        .thenReturn(TOKEN_RESPONSE);
    when(refreshCookieInstructionsMapper.toCookieInstruction(TOKEN_RESPONSE))
        .thenReturn(COOKIE_INSTRUCTION);
    when(authResultMapper.toAuthResult(TOKEN_RESPONSE)).thenReturn(AUTH_RESULT);

    var result = handler.execute(Map.of("socially_refresh_token", "refresh-1"));

    assertEquals(COOKIE_INSTRUCTION, result.cookieInstruction());
  }
}
