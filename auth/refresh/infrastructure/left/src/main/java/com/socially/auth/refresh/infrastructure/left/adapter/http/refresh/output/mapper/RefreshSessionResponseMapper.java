package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.mapper;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.RefreshSessionResponse;
import org.springframework.stereotype.Component;

@Component
public class RefreshSessionResponseMapper {
  public RefreshSessionResponse toResponse(AuthResult result) {
    long expiresIn = result.expiresIn() == null ? 0L : result.expiresIn();
    return new RefreshSessionResponse(
        result.accessToken(), result.tokenType(), expiresIn, result.user());
  }
}
