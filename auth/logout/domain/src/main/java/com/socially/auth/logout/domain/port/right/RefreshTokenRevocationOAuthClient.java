package com.socially.auth.logout.domain.port.right;

public interface RefreshTokenRevocationOAuthClient {
  void revokeRefreshToken(String refreshToken);
}
