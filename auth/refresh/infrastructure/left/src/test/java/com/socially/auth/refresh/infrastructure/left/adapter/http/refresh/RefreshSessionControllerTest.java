package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.infrastructure.left.adapter.http.input.mapper.RequestCookiesMapper;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.AuthSetCookieHeaderMapper;
import com.socially.auth.refresh.application.output.RefreshSessionCommandResult;
import com.socially.auth.refresh.application.port.left.RefreshSessionUseCase;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.RefreshSessionResponse;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.mapper.RefreshSessionResponseMapper;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RefreshSessionControllerTest {

  @Mock private RefreshSessionUseCase useCase;
  @Mock private RequestCookiesMapper requestCookiesMapper;
  @Mock private RefreshSessionResponseMapper refreshSessionResponseMapper;
  @Mock private AuthSetCookieHeaderMapper authSetCookieHeaderMapper;
  @Mock private HttpServletRequest request;

  @InjectMocks private RefreshSessionController sut;

  private static final AuthResult AUTH_RESULT =
      AuthResult.create(
              "at",
              "Bearer",
              AuthUser.create("https://idp.example", "u1", "e@x.com")
                  .withGivenName(Optional.of("N")))
          .withExpiresIn(Optional.of(120L));
  private static final User USER =
      User.create(
              Id.from("550e8400-e29b-41d4-a716-446655440000"),
              Email.from("e@x.com"),
              Instant.parse("2024-06-01T12:00:00Z"))
          .withGivenName(Optional.of("N"));
  private static final RefreshSessionResponse SESSION_RESPONSE =
      RefreshSessionResponse.create("at", "Bearer", 120L);
  private static final CookieInstruction COOKIE_INSTRUCTION =
      new CookieInstruction("socially_refresh_token", "rt-new", 86_400L);

  @Test
  void refreshSession_should_call_cookies_mapper_with_received_request() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of()))
        .thenReturn(RefreshSessionCommandResult.create(AUTH_RESULT, USER));
    when(refreshSessionResponseMapper.toResponse(AUTH_RESULT)).thenReturn(SESSION_RESPONSE);

    sut.refreshSession(request);

    verify(requestCookiesMapper).toCookieMap(request);
  }

  @Test
  void refreshSession_should_call_use_case_with_mapped_request_cookies() {
    Map<String, String> cookies = Map.of("socially_refresh_token", "rt");
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(cookies);
    when(useCase.execute(cookies))
        .thenReturn(RefreshSessionCommandResult.create(AUTH_RESULT, USER));
    when(refreshSessionResponseMapper.toResponse(AUTH_RESULT)).thenReturn(SESSION_RESPONSE);

    sut.refreshSession(request);

    verify(useCase).execute(cookies);
  }

  @Test
  void refreshSession_should_return_response_with_ok_status() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of()))
        .thenReturn(RefreshSessionCommandResult.create(AUTH_RESULT, USER));
    when(refreshSessionResponseMapper.toResponse(AUTH_RESULT)).thenReturn(SESSION_RESPONSE);

    ResponseEntity<RefreshSessionResponse> response = sut.refreshSession(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void refreshSession_should_call_set_cookie_header_mapper_with_retrieved_cookies() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of()))
        .thenReturn(
            RefreshSessionCommandResult.create(AUTH_RESULT, USER)
                .withCookieInstruction(Optional.of(COOKIE_INSTRUCTION)));
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTION))
        .thenReturn("cookie-header");
    when(refreshSessionResponseMapper.toResponse(AUTH_RESULT)).thenReturn(SESSION_RESPONSE);

    sut.refreshSession(request);

    verify(authSetCookieHeaderMapper).toSetCookieHeader(COOKIE_INSTRUCTION);
  }

  @Test
  void refreshSession_should_return_response_with_set_cookies_in_the_header() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of()))
        .thenReturn(
            RefreshSessionCommandResult.create(AUTH_RESULT, USER)
                .withCookieInstruction(Optional.of(COOKIE_INSTRUCTION)));
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTION))
        .thenReturn("cookie-header");
    when(refreshSessionResponseMapper.toResponse(AUTH_RESULT)).thenReturn(SESSION_RESPONSE);

    ResponseEntity<RefreshSessionResponse> response = sut.refreshSession(request);

    assertEquals("cookie-header", response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
  }

  @Test
  void refreshSession_should_call_response_mapper_with_retrieved_auth_result() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of()))
        .thenReturn(RefreshSessionCommandResult.create(AUTH_RESULT, USER));
    when(refreshSessionResponseMapper.toResponse(AUTH_RESULT)).thenReturn(SESSION_RESPONSE);

    sut.refreshSession(request);

    verify(refreshSessionResponseMapper).toResponse(AUTH_RESULT);
  }

  @Test
  void refreshSession_should_return_response_with_mapped_auth_result() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of()))
        .thenReturn(RefreshSessionCommandResult.create(AUTH_RESULT, USER));
    when(refreshSessionResponseMapper.toResponse(AUTH_RESULT)).thenReturn(SESSION_RESPONSE);

    ResponseEntity<RefreshSessionResponse> response = sut.refreshSession(request);

    assertEquals(SESSION_RESPONSE, response.getBody());
  }
}
