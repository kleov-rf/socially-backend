package com.socially.auth.logout.infrastructure.right.cognito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socially.auth.kernel.domain.exception.AuthInternalErrorException;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.kernel.domain.properties.CredentialsProperties;
import com.socially.auth.logout.domain.port.right.RefreshTokenRevocationOAuthClient;
import com.socially.auth.logout.infrastructure.right.cognito.mapper.RefreshTokenRevocationFormMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class CognitoRefreshTokenRevocationHttpClient implements RefreshTokenRevocationOAuthClient {

  private final AuthProperties authProperties;
  private final ObjectMapper objectMapper;
  private final RefreshTokenRevocationFormMapper formMapper;
  private final RestClient restClient;

  @Override
  public void revokeRefreshToken(String refreshToken) {
    if (!StringUtils.hasText(refreshToken)) {
      return;
    }

    var backendCreds = authProperties.oauthBackendCredentials();
    if (backendCreds.isEmpty()) {
      return;
    }
    CredentialsProperties.Backend backend = backendCreds.get();
    String backendClientId = backend.clientId();
    String backendClientSecret = extractBackendClientSecret(backend.clientSecretJson());
    if (!StringUtils.hasText(backendClientId) || !StringUtils.hasText(backendClientSecret)) {
      return;
    }

    MultiValueMap<String, String> form = formMapper.toForm(refreshToken, backendClientId);

    String basicAuth =
        Base64.getEncoder()
            .encodeToString(
                (backendClientId + ":" + backendClientSecret).getBytes(StandardCharsets.UTF_8));
    try {
      restClient
          .post()
          .uri(oauthApiBaseUrl() + "/oauth2/revoke")
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
          .body(form)
          .retrieve()
          .toBodilessEntity();
    } catch (Exception ignored) {
      // Best effort token revocation.
    }
  }

  private String extractBackendClientSecret(String secretJson) {
    if (!StringUtils.hasText(secretJson)) {
      return null;
    }
    try {
      JsonNode node = objectMapper.readTree(secretJson);
      JsonNode clientSecretNode = node.get("client_secret");
      if (clientSecretNode == null || !StringUtils.hasText(clientSecretNode.asText())) {
        return null;
      }
      return clientSecretNode.asText();
    } catch (Exception exception) {
      throw new AuthInternalErrorException(
          "Invalid backend OAuth client secret JSON configuration", exception);
    }
  }

  private String oauthApiBaseUrl() {
    if (StringUtils.hasText(authProperties.oauthApiBaseUrl())) {
      return authProperties.oauthApiBaseUrl();
    }
    return authProperties.oauthHostedDomain();
  }
}
