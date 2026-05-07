package com.socially.auth.callback.infrastructure.left.adapter.http.callback;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.auth.callback.application.output.CompleteOAuthCallbackOutcome;
import com.socially.auth.callback.application.port.left.CompleteOAuthCallbackUseCase;
import com.socially.auth.callback.infrastructure.left.adapter.http.callback.input.CallbackGoogleRequest;
import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.AuthCallbackResponse;
import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.mapper.AuthCallbackResponseMapper;
import com.socially.auth.kernel.infrastructure.left.adapter.http.input.mapper.RequestCookiesMapper;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.AuthSetCookieHeaderMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@LogOperation("AUTH_CALLBACK_GOOGLE")
public class CallbackGoogleController {
  private final CompleteOAuthCallbackUseCase useCase;
  private final RequestCookiesMapper requestCookiesMapper;
  private final AuthCallbackResponseMapper authCallbackResponseMapper;
  private final AuthSetCookieHeaderMapper authSetCookieHeaderMapper;

  @GetMapping("/callback/google")
  public ResponseEntity<AuthCallbackResponse> callbackGoogle(
      @Valid @ModelAttribute CallbackGoogleRequest callbackGoogleRequest,
      HttpServletRequest request) {
    Map<String, String> cookieMap = requestCookiesMapper.toCookieMap(request);

    CompleteOAuthCallbackOutcome outcome =
        useCase.execute(callbackGoogleRequest.code(), callbackGoogleRequest.state(), cookieMap);

    ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok();
    outcome.cookieInstructions().stream()
        .map(authSetCookieHeaderMapper::toSetCookieHeader)
        .forEach(headerValue -> responseBuilder.header(HttpHeaders.SET_COOKIE, headerValue));
    return responseBuilder.body(authCallbackResponseMapper.toResponse(outcome.authResult()));
  }
}
