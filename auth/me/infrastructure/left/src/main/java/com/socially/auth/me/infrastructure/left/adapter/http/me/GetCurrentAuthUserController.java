package com.socially.auth.me.infrastructure.left.adapter.http.me;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthUserResponse;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.mapper.AuthUserResponseMapper;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@LogOperation("AUTH_ME")
public class GetCurrentAuthUserController {
  private final GetCurrentAuthUserUseCase useCase;
  private final AuthUserResponseMapper authUserResponseMapper;

  @GetMapping("/me")
  public ResponseEntity<AuthUserResponse> me(Principal principal) {
    AuthUser execute = useCase.execute(principal);
    AuthUserResponse response = authUserResponseMapper.toResponse(execute);
    return ResponseEntity.ok(response);
  }
}
