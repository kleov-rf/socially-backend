package com.socially.auth.login.infrastructure.left.adapter.http.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.AuthSetCookieHeaderMapper;
import com.socially.auth.login.application.output.BuildOAuthLoginResult;
import com.socially.auth.login.application.port.left.BuildOAuthLoginUrlUseCase;
import com.socially.auth.login.infrastructure.left.adapter.http.login.output.LoginUrlResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LoginGoogleControllerTest {

  @Mock private BuildOAuthLoginUrlUseCase useCase;
  @Mock private AuthSetCookieHeaderMapper authSetCookieHeaderMapper;

  @InjectMocks private LoginGoogleController sut;

  @Test
  void loginWithGoogle_should_call_execute() {
    when(useCase.execute())
        .thenReturn(new BuildOAuthLoginResult("https://auth.example.com", List.of()));

    sut.loginWithGoogle();

    verify(useCase, times(1)).execute();
  }

  @Test
  void loginWithGoogle_should_return_with_ok_status() {
    when(useCase.execute())
        .thenReturn(new BuildOAuthLoginResult("https://auth.example.com", List.of()));

    ResponseEntity<?> response = sut.loginWithGoogle();

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void loginWithGoogle_should_return_response_with_retrieved_headers() {
    var instructions =
        List.of(
            new CookieInstruction("socially_oauth_state", "state-1", 300L),
            new CookieInstruction("socially_oauth_pkce", "pkce-1", 300L));
    when(useCase.execute())
        .thenReturn(new BuildOAuthLoginResult("https://auth.example.com", instructions));
    when(authSetCookieHeaderMapper.toSetCookieHeader(instructions.get(0)))
        .thenReturn("cookie-header-1");
    when(authSetCookieHeaderMapper.toSetCookieHeader(instructions.get(1)))
        .thenReturn("cookie-header-2");

    ResponseEntity<?> response = sut.loginWithGoogle();

    assertEquals(
        List.of("cookie-header-1", "cookie-header-2"),
        response.getHeaders().get(HttpHeaders.SET_COOKIE));
  }

  @Test
  void loginWithGoogle_should_return_respones_with_retrieved_url() {
    when(useCase.execute())
        .thenReturn(
            new BuildOAuthLoginResult("https://auth.example.com/oauth2/authorize", List.of()));

    ResponseEntity<?> response = sut.loginWithGoogle();

    LoginUrlResponse body = (LoginUrlResponse) response.getBody();
    assertEquals("https://auth.example.com/oauth2/authorize", body.url());
  }

  @Test
  void loginWithGoogle_should_map_cookie_instructions_to_headers() {
    var instructions =
        List.of(
            new CookieInstruction("socially_oauth_state", "state-1", 300L),
            new CookieInstruction("socially_oauth_pkce", "pkce-1", 300L));
    when(useCase.execute())
        .thenReturn(new BuildOAuthLoginResult("https://auth.example.com", instructions));
    when(authSetCookieHeaderMapper.toSetCookieHeader(instructions.get(0)))
        .thenReturn("cookie-header-1");
    when(authSetCookieHeaderMapper.toSetCookieHeader(instructions.get(1)))
        .thenReturn("cookie-header-2");

    sut.loginWithGoogle();

    verify(authSetCookieHeaderMapper).toSetCookieHeader(instructions.get(0));
    verify(authSetCookieHeaderMapper).toSetCookieHeader(instructions.get(1));
  }
}
