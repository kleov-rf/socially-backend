package com.socially.auth.logout.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.logout.domain.port.right.RefreshTokenRevocationOAuthClient;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutCommandHandlerTest {

  @Mock private AuthProperties authProperties;
  @Mock private RefreshTokenRevocationOAuthClient refreshTokenRevocationOAuthClient;

  @InjectMocks private LogoutCommandHandler handler;

  @BeforeEach
  void stubCookieNames() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    when(authProperties.stateCookieName()).thenReturn("socially_oauth_state");
    when(authProperties.pkceCookieName()).thenReturn("socially_oauth_pkce");
  }

  @Test
  void execute_should_call_oauth_client_with_received_refresh_token() {
    handler.execute(Map.of("socially_refresh_token", "the-refresh-token"));

    verify(refreshTokenRevocationOAuthClient).revokeRefreshToken("the-refresh-token");
  }

  @Test
  void execute_should_not_call_oauth_client_when_refresh_token_is_not_present() {
    handler.execute(Map.of());

    verify(refreshTokenRevocationOAuthClient, never()).revokeRefreshToken(anyString());
  }

  @Test
  void execute_should_return_result_with_refresh_cookie() {
    var result = handler.execute(Map.of());

    CookieInstruction refresh =
        result.cookieInstructions().stream()
            .filter(c -> "socially_refresh_token".equals(c.name()))
            .findFirst()
            .orElseThrow();
    assertEquals("", refresh.value());
    assertEquals(0L, refresh.maxAgeSeconds());
  }

  @Test
  void execute_should_return_result_with_state_cookie() {
    var result = handler.execute(Map.of());

    CookieInstruction state =
        result.cookieInstructions().stream()
            .filter(c -> "socially_oauth_state".equals(c.name()))
            .findFirst()
            .orElseThrow();
    assertNotNull(state);
    assertEquals("", state.value());
  }

  @Test
  void execute_should_return_result_with_pkce_cookie() {
    var result = handler.execute(Map.of());

    CookieInstruction pkce =
        result.cookieInstructions().stream()
            .filter(c -> "socially_oauth_pkce".equals(c.name()))
            .findFirst()
            .orElseThrow();
    assertNotNull(pkce);
    assertEquals("", pkce.value());
  }
}
