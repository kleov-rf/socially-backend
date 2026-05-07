package com.socially.auth.refresh.infrastructure.right.cognito.mapper;

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
class RefreshTokenExchangeFormMapperTest {

  @Mock private AuthProperties authProperties;

  private RefreshTokenExchangeFormMapper sut;

  @BeforeEach
  void setUp() {
    sut = new RefreshTokenExchangeFormMapper(authProperties);
  }

  @Test
  void toForm_should_return_form_with_grant_type() {
    MultiValueMap<String, String> form = sut.toForm(null);

    assertEquals("refresh_token", form.getFirst("grant_type"));
  }

  @Test
  void toForm_should_return_form_with_spa_client_id() {
    when(authProperties.oauthSpaClientId()).thenReturn("spa-client");

    MultiValueMap<String, String> form = sut.toForm(null);

    assertEquals("spa-client", form.getFirst("client_id"));
  }

  @Test
  void toForm_should_return_form_with_received_refresh_token() {
    MultiValueMap<String, String> form = sut.toForm("refresh-token-xyz");

    assertEquals("refresh-token-xyz", form.getFirst("refresh_token"));
  }
}
