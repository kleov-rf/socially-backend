package com.socially.auth.refresh.infrastructure.right.cognito;

import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.exception.AuthUnauthorizedException;
import com.socially.auth.kernel.domain.exception.AuthUpstreamFailureException;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.refresh.domain.port.right.RefreshTokenExchangeOAuthClient;
import com.socially.auth.refresh.infrastructure.right.cognito.mapper.RefreshTokenExchangeFormMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@RequiredArgsConstructor
@Component
public class CognitoRefreshTokenExchangeHttpClient implements RefreshTokenExchangeOAuthClient {

  private final AuthProperties authProperties;
  private final RefreshTokenExchangeFormMapper formMapper;
  private final RestClient restClient;

  @Override
  public OAuthTokenResponse exchangeRefreshToken(String refreshToken) {
    MultiValueMap<String, String> form = formMapper.toForm(refreshToken);

    try {
      return restClient
          .post()
          .uri(oauthApiBaseUrl() + "/oauth2/token")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .body(form)
          .retrieve()
          .body(OAuthTokenResponse.class);
    } catch (RestClientResponseException exception) {
      if (exception.getStatusCode().value() == 400) {
        throw new AuthUnauthorizedException("Refresh token rejected", exception);
      }
      throw new AuthUpstreamFailureException("Failed to refresh OAuth tokens", exception);
    } catch (Exception exception) {
      throw new AuthUpstreamFailureException("Failed to refresh OAuth tokens", exception);
    }
  }

  private String oauthApiBaseUrl() {
    if (StringUtils.hasText(authProperties.oauthApiBaseUrl())) {
      return authProperties.oauthApiBaseUrl();
    }
    return authProperties.oauthHostedDomain();
  }
}
