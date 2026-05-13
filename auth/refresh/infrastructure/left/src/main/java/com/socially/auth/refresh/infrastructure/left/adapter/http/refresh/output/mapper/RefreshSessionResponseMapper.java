package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.mapper;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper.UserResponseDtoMapper;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.RefreshSessionResponse;
import com.socially.user.kernel.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshSessionResponseMapper {
  private final UserResponseDtoMapper userResponseDtoMapper;

  public RefreshSessionResponse toResponse(AuthResult result, User user) {
    long expiresIn = result.expiresIn() == null ? 0L : result.expiresIn();
    return new RefreshSessionResponse(
        result.accessToken(),
        result.tokenType(),
        expiresIn,
        userResponseDtoMapper.toResponse(user));
  }
}
