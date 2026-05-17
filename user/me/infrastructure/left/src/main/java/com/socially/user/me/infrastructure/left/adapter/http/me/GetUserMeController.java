package com.socially.user.me.infrastructure.left.adapter.http.me;

import com.socially.app.infrastructure.left.adapter.http.logging.LogOperation;
import com.socially.user.me.application.port.left.GetUserMeUseCase;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.UserMeResponse;
import com.socially.user.me.infrastructure.left.adapter.http.me.output.mapper.UserMeResponseMapper;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@LogOperation("USER_ME")
public class GetUserMeController {
  private final GetUserMeUseCase getUserMeUseCase;
  private final UserMeResponseMapper userMeResponseMapper;

  @GetMapping("/me")
  public ResponseEntity<UserMeResponse> me(Principal principal) {
    var result = getUserMeUseCase.execute(principal);
    UserMeResponse response = userMeResponseMapper.toResponse(result);
    return ResponseEntity.ok(response);
  }
}
