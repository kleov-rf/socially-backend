package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.mapper;

import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.AuthCallbackResponse;
import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.UserResponseDtoMapper;
import com.socially.user.kernel.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthCallbackResponseMapper {
  private final UserResponseDtoMapper userResponseDtoMapper;

  public AuthCallbackResponse toResponse(AuthResult result, User user) {
    long expiresIn = result.expiresIn() == null ? 0L : result.expiresIn();
    return new AuthCallbackResponse(
        result.accessToken(),
        result.tokenType(),
        expiresIn,
        userResponseDtoMapper.toResponse(user));
  }
}
