package com.socially.auth.login.application.mapper;

import com.socially.auth.kernel.domain.properties.AuthProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class LoginAuthorizeUrlMapper {
  private final AuthProperties authProperties;

  public String toAuthorizeUrl(String stateNonce, String pkceCodeChallenge) {
    StringBuilder authorize =
        new StringBuilder(Objects.requireNonNull(authProperties.oauthHostedDomain()))
            .append("/oauth2/authorize?");

    appendQueryParam(authorize, "response_type", "code");
    appendQueryParam(authorize, "scope", "openid email profile");
    appendQueryParam(authorize, "code_challenge_method", "S256");
    appendQueryParam(authorize, "state", stateNonce);
    appendQueryParam(authorize, "code_challenge", pkceCodeChallenge);
    appendQueryParam(authorize, "client_id", authProperties.oauthSpaClientId());
    appendQueryParam(authorize, "redirect_uri", authProperties.redirectUri());

    if (StringUtils.hasText(authProperties.oauthIdentityProvider())) {
      appendQueryParam(authorize, "identity_provider", authProperties.oauthIdentityProvider());
    }
    return authorize.toString();
  }

  private void appendQueryParam(StringBuilder urlBuilder, String key, String value) {
    boolean isFirstParam = urlBuilder.indexOf("=") == -1;
    urlBuilder.append(
        "%s%s=%s".formatted(isFirstParam ? "" : "&", urlEncode(key), urlEncode(value)));
  }

  private String urlEncode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
