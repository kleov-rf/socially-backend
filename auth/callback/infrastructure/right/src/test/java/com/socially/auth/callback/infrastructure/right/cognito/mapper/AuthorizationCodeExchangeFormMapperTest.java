package com.socially.auth.callback.infrastructure.right.cognito.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.properties.AuthProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
class AuthorizationCodeExchangeFormMapperTest {

  @Mock private AuthProperties authProperties;

  private AuthorizationCodeExchangeFormMapper sut;

  @BeforeEach
  void setUp() {
    sut = new AuthorizationCodeExchangeFormMapper(authProperties);
  }

  @Test
  void toForm_should_set_grant_type_to_authorization_code() {
    MultiValueMap<String, String> form = sut.toForm(null, null);

    assertEquals("authorization_code", form.getFirst("grant_type"));
  }

  @Test
  void toForm_should_set_client_id_from_auth_properties() {
    when(authProperties.oauthSpaClientId()).thenReturn("spa-client");

    MultiValueMap<String, String> form = sut.toForm(null, null);

    assertEquals("spa-client", form.getFirst("client_id"));
  }

  @Test
  void toForm_should_set_code() {
    MultiValueMap<String, String> form = sut.toForm("auth-code-xyz", null);

    assertEquals("auth-code-xyz", form.getFirst("code"));
  }

  @Test
  void toForm_should_set_redirect_uri_from_auth_properties() {
    when(authProperties.redirectUri()).thenReturn("http://app/callback");

    MultiValueMap<String, String> form = sut.toForm(null, null);

    assertEquals("http://app/callback", form.getFirst("redirect_uri"));
  }

  @Test
  void toForm_should_set_code_verifier() {
    MultiValueMap<String, String> form = sut.toForm(null, "pkce-verifier");

    assertEquals("pkce-verifier", form.getFirst("code_verifier"));
  }
}
