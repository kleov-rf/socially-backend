package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.AuthCallbackResponse;
import com.socially.auth.kernel.domain.AuthResult;
import org.junit.jupiter.api.Test;

class AuthCallbackResponseMapperTest {

  private final AuthCallbackResponseMapper sut = new AuthCallbackResponseMapper();

  @Test
  void toResponse_should_map_access_token() {
    AuthResult result = new AuthResult("test-access-token", null, null, null);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals("test-access-token", response.accessToken());
  }

  @Test
  void toResponse_should_map_token_type() {
    AuthResult result = new AuthResult(null, "Bearer", null, null);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals("Bearer", response.tokenType());
  }

  @Test
  void toResponse_should_map_expires_in() {
    AuthResult result = new AuthResult(null, null, 3600L, null);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(3600L, response.expiresIn());
  }

  @Test
  void toResponse_should_map_expires_in_when_expires_in_is_not_present() {
    AuthResult result = new AuthResult(null, null, null, null);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(0L, response.expiresIn());
  }
}
