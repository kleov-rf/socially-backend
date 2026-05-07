package com.socially.auth.login.application;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.login.application.mapper.LoginAuthorizeUrlMapper;
import com.socially.auth.login.application.security.AuthCookieFactory;
import com.socially.auth.login.application.security.OAuthPkceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuildOAuthLoginUrlCommandHandlerTest {

  @Mock AuthProperties authProperties;
  @Mock OAuthPkceService oAuthPkceService;
  @Mock AuthCookieFactory authCookieFactory;
  @Mock LoginAuthorizeUrlMapper loginAuthorizeUrlMapper;

  @InjectMocks BuildOAuthLoginUrlCommandHandler sut;

  @BeforeEach
  void setUp() {
    when(oAuthPkceService.generateRandomToken())
        .thenReturn("state-nonce")
        .thenReturn("code-verifier");
    when(oAuthPkceService.generateCodeChallenge("code-verifier")).thenReturn("code-challenge");
    when(authCookieFactory.oauthTransientCookie("socially_oauth_state", "state-nonce"))
        .thenReturn(new CookieInstruction("socially_oauth_state", "state-nonce", 300));
    when(authCookieFactory.oauthTransientCookie("socially_oauth_pkce", "code-verifier"))
        .thenReturn(new CookieInstruction("socially_oauth_pkce", "code-verifier", 300));
    when(authProperties.stateCookieName()).thenReturn("socially_oauth_state");
    when(authProperties.pkceCookieName()).thenReturn("socially_oauth_pkce");
    when(loginAuthorizeUrlMapper.toAuthorizeUrl("state-nonce", "code-challenge"))
        .thenReturn(
            "https://auth.example.amazoncognito.com/oauth2/authorize?response_type=code&scope=openid+email+profile");
  }

  @Test
  void execute_should_call_twice_generate_random_token_to_get_state_nonce_and_pkce_code_verifier() {
    sut.execute();

    verify(oAuthPkceService, times(2)).generateRandomToken();
  }

  @Test
  void execute_should_call_generate_code_challenge_with_retrieved_code_verifier() {
    sut.execute();

    verify(oAuthPkceService).generateCodeChallenge("code-verifier");
  }

  @Test
  void
      execute_should_call_cookie_factory_transient_cookie_with_state_cookie_name_and_retrieved_state_nonce() {
    sut.execute();

    verify(authCookieFactory).oauthTransientCookie("socially_oauth_state", "state-nonce");
  }

  @Test
  void execute_should_return_state_transient_set_cookie_header() {
    var result = sut.execute();

    assertTrue(
        result.cookieInstructions().stream()
            .anyMatch(
                instruction ->
                    instruction.name().equals("socially_oauth_state")
                        && instruction.value().equals("state-nonce")));
  }

  @Test
  void
      execute_should_call_cookie_factory_transient_cookie_with_pkce_cookie_name_and_retrieved_code_verifier() {
    sut.execute();

    verify(authCookieFactory).oauthTransientCookie("socially_oauth_pkce", "code-verifier");
  }

  @Test
  void execute_should_return_pkce_transient_set_cookie_header() {
    var result = sut.execute();

    assertTrue(
        result.cookieInstructions().stream()
            .anyMatch(
                instruction ->
                    instruction.name().equals("socially_oauth_pkce")
                        && instruction.value().equals("code-verifier")));
  }

  @Test
  void execute_should_call_authorize_url_mapper_with_state_nonce_and_pkce_code_challenge() {
    sut.execute();
    verify(loginAuthorizeUrlMapper).toAuthorizeUrl("state-nonce", "code-challenge");
  }

  @Test
  void execute_should_return_url_from_authorize_url_mapper() {
    String url = sut.execute().authorizeUrl();

    assertTrue(url.contains("response_type=code"));
  }
}
