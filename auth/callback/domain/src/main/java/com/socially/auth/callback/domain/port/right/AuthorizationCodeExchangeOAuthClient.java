package com.socially.auth.callback.domain.port.right;

import com.socially.auth.kernel.domain.OAuthTokenResponse;

public interface AuthorizationCodeExchangeOAuthClient {
  OAuthTokenResponse exchangeAuthorizationCode(String code, String codeVerifier);
}
