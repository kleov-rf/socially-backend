package com.socially.auth.callback.infrastructure.right.cognito;

import com.socially.auth.callback.domain.port.right.AuthorizationCodeExchangeOAuthClient;
import com.socially.auth.callback.infrastructure.right.cognito.mapper.AuthorizationCodeExchangeFormMapper;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.exception.AuthUpstreamFailureException;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class CognitoAuthorizationCodeExchangeHttpClient
    implements AuthorizationCodeExchangeOAuthClient {

  private final AuthProperties authProperties;
  private final AuthorizationCodeExchangeFormMapper formMapper;
  private final RestClient restClient;

  @Override
  public OAuthTokenResponse exchangeAuthorizationCode(String code, String codeVerifier) {
    MultiValueMap<String, String> form = formMapper.toForm(code, codeVerifier);

    try {
      return restClient
          .post()
          .uri(oauthApiBaseUrl() + "/oauth2/token")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .body(form)
          .retrieve()
          .body(OAuthTokenResponse.class);
    } catch (Exception exception) {
      throw new AuthUpstreamFailureException(
          "Failed to exchange OAuth authorization code", exception);
    }
  }

  private String oauthApiBaseUrl() {
    if (StringUtils.hasText(authProperties.oauthApiBaseUrl())) {
      return authProperties.oauthApiBaseUrl();
    }
    return authProperties.oauthHostedDomain();
  }
}
