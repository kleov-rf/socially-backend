package com.socially.auth.callback.infrastructure.right.cognito.mapper;

import com.socially.auth.kernel.domain.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
@Component
public class AuthorizationCodeExchangeFormMapper {

  private final AuthProperties authProperties;

  public MultiValueMap<String, String> toForm(String code, String codeVerifier) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", authProperties.oauthSpaClientId());
    form.add("code", code);
    form.add("redirect_uri", authProperties.redirectUri());
    form.add("code_verifier", codeVerifier);
    return form;
  }
}
