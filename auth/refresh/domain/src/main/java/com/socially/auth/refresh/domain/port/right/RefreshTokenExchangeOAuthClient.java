package com.socially.auth.refresh.domain.port.right;

import com.socially.auth.kernel.domain.OAuthTokenResponse;

public interface RefreshTokenExchangeOAuthClient {
  OAuthTokenResponse exchangeRefreshToken(String refreshToken);
}
