package com.socially.auth.logout.infrastructure.right.cognito.mapper;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class RefreshTokenRevocationFormMapper {

  public MultiValueMap<String, String> toForm(String refreshToken, String backendClientId) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("token", refreshToken);
    form.add("client_id", backendClientId);
    return form;
  }
}
