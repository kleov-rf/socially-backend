package com.socially.auth.callback.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CallbackCookieInstructionsMapperTest {

  @Mock private AuthProperties authProperties;

  @InjectMocks private CallbackCookieInstructionsMapper sut;

  @Test
  void toCookieInstructions_should_return_clear_instructions_for_state_and_pkce() {
    stubStateAndPkceCookies();
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access-token-1", "id-token-1", null, "Bearer", 3600L);

    List<CookieInstruction> instructions = sut.toCookieInstructions(tokenResponse);

    assertTrue(
        instructions.stream()
            .anyMatch(
                instruction ->
                    instruction.name().equals("socially_oauth_state")
                        && instruction.value().isEmpty()
                        && instruction.maxAgeSeconds() == 0L));
    assertTrue(
        instructions.stream()
            .anyMatch(
                instruction ->
                    instruction.name().equals("socially_oauth_pkce")
                        && instruction.value().isEmpty()
                        && instruction.maxAgeSeconds() == 0L));
  }

  @Test
  void toCookieInstructions_should_include_refresh_instruction_when_refresh_token_is_present() {
    stubStateAndPkceCookies();
    when(authProperties.refreshCookieName()).thenReturn("socially_refresh_token");
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access-token-1", "id-token-1", "refresh-1", "Bearer", 3600L);

    List<CookieInstruction> instructions = sut.toCookieInstructions(tokenResponse);

    assertTrue(
        instructions.stream()
            .anyMatch(
                instruction ->
                    instruction.name().equals("socially_refresh_token")
                        && instruction.value().equals("refresh-1")
                        && instruction.maxAgeSeconds() == 2_592_000L));
  }

  @Test
  void toCookieInstructions_should_not_include_refresh_instruction_when_refresh_token_is_missing() {
    stubStateAndPkceCookies();
    OAuthTokenResponse tokenResponse =
        new OAuthTokenResponse("access-token-1", "id-token-1", null, "Bearer", 3600L);

    List<CookieInstruction> instructions = sut.toCookieInstructions(tokenResponse);

    assertEquals(2, instructions.size());
    assertTrue(
        instructions.stream()
            .noneMatch(instruction -> instruction.name().equals("socially_refresh_token")));
  }

  private void stubStateAndPkceCookies() {
    when(authProperties.stateCookieName()).thenReturn("socially_oauth_state");
    when(authProperties.pkceCookieName()).thenReturn("socially_oauth_pkce");
  }
}
