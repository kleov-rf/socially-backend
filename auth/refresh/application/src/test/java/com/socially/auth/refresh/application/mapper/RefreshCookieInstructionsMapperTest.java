package com.socially.auth.refresh.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshCookieInstructionsMapperTest {

  @Mock private AuthProperties authProperties;

  @InjectMocks private RefreshCookieInstructionsMapper sut;

  @Test
  void toCookieInstruction_should_return_empty_when_received_refresh_token_is_not_present() {
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access", "id-token", null, "Bearer", 3600L);

    Optional<CookieInstruction> result = sut.toCookieInstruction(tokenResponse);

    assertEquals(Optional.empty(), result);
  }

  @Test
  void toCookieInstruction_should_return_cookie_with_refresh_cookie_name() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access", "id-token", "refresh-2", "Bearer", 3600L);

    Optional<CookieInstruction> result = sut.toCookieInstruction(tokenResponse);

    assertEquals(Optional.of("socially_refresh_token"), result.map(CookieInstruction::name));
  }

  @Test
  void toCookieInstruction_should_return_cookie_with_received_refresh_token() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access", "id-token", "refresh-xyz", "Bearer", 3600L);

    Optional<CookieInstruction> result = sut.toCookieInstruction(tokenResponse);

    assertEquals(Optional.of("refresh-xyz"), result.map(CookieInstruction::value));
  }

  @Test
  void toCookieInstruction_should_return_cookie_with_a_duration_of_a_day() {
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access", "id-token", "r", "Bearer", 3600L);

    Optional<CookieInstruction> result = sut.toCookieInstruction(tokenResponse);

    assertEquals(Optional.of(60L * 60L * 24L), result.map(CookieInstruction::maxAgeSeconds));
  }
}
