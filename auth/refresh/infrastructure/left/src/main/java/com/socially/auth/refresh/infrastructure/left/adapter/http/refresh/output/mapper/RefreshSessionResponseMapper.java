package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.mapper;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.RefreshSessionResponse;
import org.springframework.stereotype.Component;

@Component
public class RefreshSessionResponseMapper {

  public RefreshSessionResponse toResponse(AuthResult result) {
    long expiresIn = result.expiresIn().orElse(0L);
    return RefreshSessionResponse.create(result.accessToken(), result.tokenType(), expiresIn);
  }
}
