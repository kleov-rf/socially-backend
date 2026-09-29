package com.socially.auth.callback.infrastructure.left.adapter.http.callback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.callback.application.output.CompleteOAuthCallbackOutcome;
import com.socially.auth.callback.application.port.left.CompleteOAuthCallbackUseCase;
import com.socially.auth.callback.infrastructure.left.adapter.http.callback.input.CallbackGoogleRequest;
import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.AuthCallbackResponse;
import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.mapper.AuthCallbackResponseMapper;
import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.infrastructure.left.adapter.http.input.mapper.RequestCookiesMapper;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.AuthSetCookieHeaderMapper;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CallbackGoogleControllerTest {

  @Mock private CompleteOAuthCallbackUseCase useCase;
  @Mock private RequestCookiesMapper requestCookiesMapper;
  @Mock private AuthCallbackResponseMapper authCallbackResponseMapper;
  @Mock private AuthSetCookieHeaderMapper authSetCookieHeaderMapper;
  @Mock private HttpServletRequest request;

  @InjectMocks private CallbackGoogleController sut;

  private static final CallbackGoogleRequest REQUEST_PARAMS =
      new CallbackGoogleRequest("code-123", "state-123");

  private static final AuthResult AUTH_RESULT =
      AuthResult.create(
              "access-token",
              "Bearer",
              AuthUser.create("https://idp.example", "id-1", "user@example.com")
                  .withGivenName(Optional.of("John"))
                  .withFamilyName(Optional.of("Doe")))
          .withExpiresIn(Optional.of(3600L));
  private static final User USER =
      User.create(
          Id.from("550e8400-e29b-41d4-a716-446655440000"),
          Email.from("user@example.com"),
          "John",
          "Doe",
          Instant.parse("2024-06-01T12:00:00Z"));

  private static final AuthCallbackResponse RESPONSE =
      new AuthCallbackResponse("access-token", "Bearer", 3600L);
  private static final List<CookieInstruction> COOKIE_INSTRUCTIONS =
      List.of(
          new CookieInstruction("socially_oauth_state", "", 0L),
          new CookieInstruction("socially_oauth_pkce", "", 0L));

  @BeforeEach
  void setUp() {
    when(requestCookiesMapper.toCookieMap(request))
        .thenReturn(
            Map.of("socially_oauth_state", "state-cookie", "socially_oauth_pkce", "pkce-cookie"));

    when(useCase.execute(
            REQUEST_PARAMS.code(),
            REQUEST_PARAMS.state(),
            Map.of("socially_oauth_state", "state-cookie", "socially_oauth_pkce", "pkce-cookie")))
        .thenReturn(new CompleteOAuthCallbackOutcome(AUTH_RESULT, USER, COOKIE_INSTRUCTIONS));
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTIONS.get(0)))
        .thenReturn("cookie-1");
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTIONS.get(1)))
        .thenReturn("cookie-2");

    when(authCallbackResponseMapper.toResponse(AUTH_RESULT)).thenReturn(RESPONSE);
  }

  @Test
  void callbackGoogle_should_call_request_cookies_mapper_with_received_request() {
    sut.callbackGoogle(REQUEST_PARAMS, request);

    verify(requestCookiesMapper, times(1)).toCookieMap(request);
  }

  @Test
  void
      callbackGoogle_should_call_use_case_execute_with_mapped_cookies_and_request_code_and_state() {
    sut.callbackGoogle(REQUEST_PARAMS, request);

    verify(useCase)
        .execute(
            REQUEST_PARAMS.code(),
            REQUEST_PARAMS.state(),
            Map.of("socially_oauth_state", "state-cookie", "socially_oauth_pkce", "pkce-cookie"));
  }

  @Test
  void callbackGoogle_should_return_response_with_ok_status() {
    ResponseEntity<AuthCallbackResponse> response = sut.callbackGoogle(REQUEST_PARAMS, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void callbackGoogle_should_return_response_with_retrieved_cookies() {
    ResponseEntity<AuthCallbackResponse> response = sut.callbackGoogle(REQUEST_PARAMS, request);

    assertEquals(
        List.of("cookie-1", "cookie-2"), response.getHeaders().get(HttpHeaders.SET_COOKIE));
  }

  @Test
  void callbackGoogle_should_map_cookie_instructions_into_set_cookie_headers() {
    sut.callbackGoogle(REQUEST_PARAMS, request);

    verify(authSetCookieHeaderMapper).toSetCookieHeader(COOKIE_INSTRUCTIONS.get(0));
    verify(authSetCookieHeaderMapper).toSetCookieHeader(COOKIE_INSTRUCTIONS.get(1));
  }

  @Test
  void callbackGoogle_should_call_response_mapper_with_retrieved_auth_result() {
    sut.callbackGoogle(REQUEST_PARAMS, request);

    verify(authCallbackResponseMapper).toResponse(AUTH_RESULT);
  }

  @Test
  void callbackGoogle_should_return_response_with_mapped_response() {

    ResponseEntity<AuthCallbackResponse> response = sut.callbackGoogle(REQUEST_PARAMS, request);

    assertEquals(RESPONSE, response.getBody());
  }
}
