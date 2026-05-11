package com.socially.auth.logout.infrastructure.left.adapter.http.logout;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.auth.kernel.infrastructure.left.adapter.http.input.mapper.RequestCookiesMapper;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.AuthSetCookieHeaderMapper;
import com.socially.auth.logout.application.port.left.LogoutUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@LogOperation("AUTH_LOGOUT")
public class LogoutController {
  private final LogoutUseCase useCase;
  private final RequestCookiesMapper requestCookiesMapper;
  private final AuthSetCookieHeaderMapper authSetCookieHeaderMapper;

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    var result = useCase.execute(requestCookiesMapper.toCookieMap(request));
    ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
    result.cookieInstructions().stream()
        .map(authSetCookieHeaderMapper::toSetCookieHeader)
        .forEach(headerValue -> responseBuilder.header(HttpHeaders.SET_COOKIE, headerValue));
    return responseBuilder.build();
  }
}
