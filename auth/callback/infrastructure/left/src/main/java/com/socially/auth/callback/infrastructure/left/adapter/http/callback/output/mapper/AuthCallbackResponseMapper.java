package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.mapper;

import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.AuthCallbackResponse;
import com.socially.auth.kernel.domain.AuthResult;
import org.springframework.stereotype.Component;

@Component
public class AuthCallbackResponseMapper {

  public AuthCallbackResponse toResponse(AuthResult result) {
    long expiresIn = result.expiresIn() == null ? 0L : result.expiresIn();
    return new AuthCallbackResponse(
        result.accessToken(), result.tokenType(), expiresIn, result.user());
  }
}
