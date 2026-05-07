package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.AuthCallbackResponse;
import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthCallbackResponseMapperTest {

  private AuthCallbackResponseMapper sut;

  @BeforeEach
  void setUp() {
    sut = new AuthCallbackResponseMapper();
  }

  @Test
  void toResponse_should_map_access_token() {
    String accessToken = "test-access-token";
    AuthResult result = new AuthResult(accessToken, null, null, null);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(accessToken, response.accessToken());
  }

  @Test
  void toResponse_should_map_token_type() {
    String tokenType = "Bearer";
    AuthResult result = new AuthResult(null, tokenType, null, null);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(tokenType, response.tokenType());
  }

  @Test
  void toResponse_should_map_expires_in() {
    long expiresIn = 3600L;
    AuthResult result = new AuthResult(null, null, expiresIn, null);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(expiresIn, response.expiresIn());
  }

  @Test
  void toResponse_should_map_expires_in_when_expires_in_is_not_present() {
    Long expiresIn = null;
    AuthResult result = new AuthResult(null, null, expiresIn, null);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(0L, response.expiresIn());
  }

  @Test
  void toResponse_should_map_user() {
    AuthUser authUser = new AuthUser("user-123", "test@example.com", "Test User");
    AuthResult result = new AuthResult(null, null, null, authUser);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(authUser, response.user());
  }
}
