package com.socially.auth.logout.infrastructure.right.cognito.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;

class RefreshTokenRevocationFormMapperTest {

  private RefreshTokenRevocationFormMapper sut;

  @BeforeEach
  void setUp() {
    sut = new RefreshTokenRevocationFormMapper();
  }

  @Test
  void toForm_should_return_form_with_received_refresh_token() {
    MultiValueMap<String, String> form = sut.toForm("refresh-token-xyz", null);

    assertEquals("refresh-token-xyz", form.getFirst("token"));
  }

  @Test
  void toForm_should_return_form_with_received_backend_client_id() {
    MultiValueMap<String, String> form = sut.toForm(null, "backend-client-id");

    assertEquals("backend-client-id", form.getFirst("client_id"));
  }
}
