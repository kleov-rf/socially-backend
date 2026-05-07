package com.socially.auth.logout.infrastructure.left.adapter.http.logout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.CookieInstruction;
import com.socially.auth.kernel.infrastructure.left.adapter.http.input.mapper.RequestCookiesMapper;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.AuthSetCookieHeaderMapper;
import com.socially.auth.logout.application.output.LogoutResult;
import com.socially.auth.logout.application.port.left.LogoutUseCase;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LogoutControllerTest {

  @Mock private LogoutUseCase useCase;
  @Mock private RequestCookiesMapper requestCookiesMapper;
  @Mock private AuthSetCookieHeaderMapper authSetCookieHeaderMapper;
  @Mock private HttpServletRequest request;

  @InjectMocks private LogoutController sut;

  private static final List<CookieInstruction> COOKIE_INSTRUCTIONS =
      List.of(
          new CookieInstruction("socially_refresh_token", "", 0L),
          new CookieInstruction("socially_oauth_state", "", 0L),
          new CookieInstruction("socially_oauth_pkce", "", 0L));

  @Test
  void logout_should_call_cookies_mapper_with_received_request() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of())).thenReturn(new LogoutResult(List.of()));

    sut.logout(request);

    verify(requestCookiesMapper).toCookieMap(request);
  }

  @Test
  void logout_should_call_use_case_with_mapped_request_cookies() {
    Map<String, String> cookies = Map.of("socially_refresh_token", "rt");
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(cookies);
    when(useCase.execute(cookies)).thenReturn(new LogoutResult(List.of()));

    sut.logout(request);

    verify(useCase).execute(cookies);
  }

  @Test
  void logout_should_return_response_with_ok_status() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of())).thenReturn(new LogoutResult(List.of()));

    ResponseEntity<Void> response = sut.logout(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void logout_should_call_set_cookie_header_mapper_with_retrieved_cookies() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of())).thenReturn(new LogoutResult(COOKIE_INSTRUCTIONS));
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTIONS.get(0))).thenReturn("h1");
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTIONS.get(1))).thenReturn("h2");
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTIONS.get(2))).thenReturn("h3");

    sut.logout(request);

    verify(authSetCookieHeaderMapper).toSetCookieHeader(COOKIE_INSTRUCTIONS.get(0));
    verify(authSetCookieHeaderMapper).toSetCookieHeader(COOKIE_INSTRUCTIONS.get(1));
    verify(authSetCookieHeaderMapper).toSetCookieHeader(COOKIE_INSTRUCTIONS.get(2));
  }

  @Test
  void logout_should_return_response_with_set_cookies_in_the_header() {
    when(requestCookiesMapper.toCookieMap(request)).thenReturn(Map.of());
    when(useCase.execute(Map.of())).thenReturn(new LogoutResult(COOKIE_INSTRUCTIONS));
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTIONS.get(0))).thenReturn("h1");
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTIONS.get(1))).thenReturn("h2");
    when(authSetCookieHeaderMapper.toSetCookieHeader(COOKIE_INSTRUCTIONS.get(2))).thenReturn("h3");

    ResponseEntity<Void> response = sut.logout(request);

    assertEquals(List.of("h1", "h2", "h3"), response.getHeaders().get(HttpHeaders.SET_COOKIE));
  }
}
