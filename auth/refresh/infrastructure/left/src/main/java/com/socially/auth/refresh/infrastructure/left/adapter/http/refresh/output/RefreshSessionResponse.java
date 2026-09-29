package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record RefreshSessionResponse(String accessToken, String tokenType, long expiresIn) {

  public static RefreshSessionResponse create(
      String accessToken, String tokenType, long expiresIn) {
    return new RefreshSessionResponse(accessToken, tokenType, expiresIn);
  }
}
