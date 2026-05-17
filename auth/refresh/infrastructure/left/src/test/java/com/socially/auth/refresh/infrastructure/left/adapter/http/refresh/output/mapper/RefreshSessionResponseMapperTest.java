package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.RefreshSessionResponse;
import org.junit.jupiter.api.Test;

class RefreshSessionResponseMapperTest {

  private final RefreshSessionResponseMapper sut = new RefreshSessionResponseMapper();

  @Test
  void toResponse_should_map_access_token() {
    AuthResult result =
        new AuthResult(
            "access-token", "Bearer", 60L, new AuthUser("https://idp.example", "i", "e", "n", "x"));

    RefreshSessionResponse response = sut.toResponse(result);

    assertEquals("access-token", response.accessToken());
  }

  @Test
  void toResponse_should_map_token_type() {
    AuthResult result =
        new AuthResult("a", "JWT", 60L, new AuthUser("https://idp.example", "i", "e", "n", "x"));

    RefreshSessionResponse response = sut.toResponse(result);

    assertEquals("JWT", response.tokenType());
  }

  @Test
  void toResponse_should_map_expires_in() {
    AuthResult result =
        new AuthResult(
            "a", "Bearer", 999L, new AuthUser("https://idp.example", "i", "e", "n", "x"));

    RefreshSessionResponse response = sut.toResponse(result);

    assertEquals(999L, response.expiresIn());
  }

  @Test
  void toResponse_should_map_expires_in_when_expires_in_is_not_present() {
    AuthResult result =
        new AuthResult(
            "a", "Bearer", null, new AuthUser("https://idp.example", "i", "e", "n", "x"));

    RefreshSessionResponse response = sut.toResponse(result);

    assertEquals(0L, response.expiresIn());
  }
}
