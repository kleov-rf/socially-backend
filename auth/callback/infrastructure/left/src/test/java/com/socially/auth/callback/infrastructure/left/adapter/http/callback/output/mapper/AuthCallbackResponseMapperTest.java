package com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.auth.callback.infrastructure.left.adapter.http.callback.output.AuthCallbackResponse;
import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthCallbackResponseMapperTest {

  private final AuthCallbackResponseMapper sut = new AuthCallbackResponseMapper();
  private static final AuthUser AUTH_USER =
      AuthUser.create("https://idp.example", "sub-1", "user@example.com");

  @Test
  void toResponse_should_map_access_token() {
    AuthResult result = AuthResult.create("test-access-token", "Bearer", AUTH_USER);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals("test-access-token", response.accessToken());
  }

  @Test
  void toResponse_should_map_token_type() {
    AuthResult result = AuthResult.create("test-access-token", "Bearer", AUTH_USER);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals("Bearer", response.tokenType());
  }

  @Test
  void toResponse_should_map_expires_in() {
    AuthResult result =
        AuthResult.create("test-access-token", "Bearer", AUTH_USER)
            .withExpiresIn(Optional.of(3600L));

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(3600L, response.expiresIn());
  }

  @Test
  void toResponse_should_map_expires_in_when_expires_in_is_not_present() {
    AuthResult result = AuthResult.create("test-access-token", "Bearer", AUTH_USER);

    AuthCallbackResponse response = sut.toResponse(result);

    assertEquals(0L, response.expiresIn());
  }
}
