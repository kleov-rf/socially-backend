package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.auth.kernel.infrastructure.left.adapter.http.input.mapper.RequestCookiesMapper;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.AuthSetCookieHeaderMapper;
import com.socially.auth.refresh.application.port.left.RefreshSessionUseCase;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.RefreshSessionResponse;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.mapper.RefreshSessionResponseMapper;
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
@LogOperation("AUTH_REFRESH_SESSION")
public class RefreshSessionController {
  private final RefreshSessionUseCase useCase;
  private final RequestCookiesMapper requestCookiesMapper;
  private final RefreshSessionResponseMapper refreshSessionResponseMapper;
  private final AuthSetCookieHeaderMapper authSetCookieHeaderMapper;

  @PostMapping("/refresh")
  public ResponseEntity<RefreshSessionResponse> refreshSession(HttpServletRequest request) {
    var commandResult = useCase.execute(requestCookiesMapper.toCookieMap(request));
    ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
    if (commandResult.cookieInstruction() != null) {
      responseBuilder.header(
          HttpHeaders.SET_COOKIE,
          authSetCookieHeaderMapper.toSetCookieHeader(commandResult.cookieInstruction()));
    }
    return responseBuilder.body(
        refreshSessionResponseMapper.toResponse(commandResult.authResult(), commandResult.user()));
  }
}
