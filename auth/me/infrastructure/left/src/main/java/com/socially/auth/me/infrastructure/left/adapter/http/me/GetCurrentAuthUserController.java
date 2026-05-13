package com.socially.auth.me.infrastructure.left.adapter.http.me;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.auth.kernel.domain.UserResponseDto;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.UserResponseDtoMapper;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
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
  private final GetCurrentAuthUserUseCase useCase;
  private final UserResponseDtoMapper userResponseDtoMapper;

  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> me(Principal principal) {
    var execute = useCase.execute(principal);
    UserResponseDto response = userResponseDtoMapper.toResponse(execute);
    return ResponseEntity.ok(response);
  }
}
