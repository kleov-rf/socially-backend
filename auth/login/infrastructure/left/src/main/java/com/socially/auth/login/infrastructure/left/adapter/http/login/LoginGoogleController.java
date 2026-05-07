package com.socially.auth.login.infrastructure.left.adapter.http.login;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.AuthSetCookieHeaderMapper;
import com.socially.auth.login.application.output.BuildOAuthLoginResult;
import com.socially.auth.login.application.port.left.BuildOAuthLoginUrlUseCase;
import com.socially.auth.login.infrastructure.left.adapter.http.login.output.LoginUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@LogOperation("AUTH_LOGIN_GOOGLE")
public class LoginGoogleController {
  private final BuildOAuthLoginUrlUseCase useCase;
  private final AuthSetCookieHeaderMapper authSetCookieHeaderMapper;

  @GetMapping("/login/google")
  public ResponseEntity<LoginUrlResponse> loginWithGoogle() {
    BuildOAuthLoginResult result = useCase.execute();

    ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
    result.cookieInstructions().stream()
        .map(authSetCookieHeaderMapper::toSetCookieHeader)
        .forEach(headerValue -> responseBuilder.header(HttpHeaders.SET_COOKIE, headerValue));
    return responseBuilder.body(new LoginUrlResponse(result.authorizeUrl()));
  }
}
