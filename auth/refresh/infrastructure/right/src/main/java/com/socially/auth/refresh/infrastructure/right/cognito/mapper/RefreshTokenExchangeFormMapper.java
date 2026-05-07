package com.socially.auth.refresh.infrastructure.right.cognito.mapper;

import com.socially.auth.kernel.domain.properties.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
@Component
public class RefreshTokenExchangeFormMapper {

  private final AuthProperties authProperties;

  public MultiValueMap<String, String> toForm(String refreshToken) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "refresh_token");
    form.add("client_id", authProperties.oauthSpaClientId());
    form.add("refresh_token", refreshToken);
    return form;
  }
}
