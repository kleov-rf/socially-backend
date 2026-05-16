package com.socially.auth.me.infrastructure.left.adapter.http.me;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.auth.me.application.port.left.GetAuthMeUseCase;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthMeResponse;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.mapper.AuthMeResponseMapper;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@LogOperation("AUTH_ME")
public class GetCurrentAuthUserController {
  private final GetAuthMeUseCase getAuthMeUseCase;
  private final AuthMeResponseMapper authMeResponseMapper;

  @GetMapping("/me")
  public ResponseEntity<AuthMeResponse> me(Principal principal) {
    var result = getAuthMeUseCase.execute(principal);
    AuthMeResponse response = authMeResponseMapper.toResponse(result);
    return ResponseEntity.ok(response);
  }
}
