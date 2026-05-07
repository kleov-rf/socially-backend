package com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.auth.kernel.domain.AuthResult;
import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.refresh.infrastructure.left.adapter.http.refresh.output.RefreshSessionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RefreshSessionResponseMapperTest {

  private RefreshSessionResponseMapper sut;

  @BeforeEach
  void setUp() {
    sut = new RefreshSessionResponseMapper();
  }

  @Test
  void toResponse_should_map_result_access_token() {
    AuthResult result = new AuthResult("access-token", "Bearer", 60L, new AuthUser("i", "e", "n"));

    RefreshSessionResponse response = sut.toResponse(result);

    assertEquals("access-token", response.accessToken());
  }

  @Test
  void toResponse_should_map_result_token_type() {
    AuthResult result = new AuthResult("a", "JWT", 60L, new AuthUser("i", "e", "n"));

    RefreshSessionResponse response = sut.toResponse(result);

    assertEquals("JWT", response.tokenType());
  }

  @Test
  void toResponse_should_map_result_expires_in() {
    AuthResult result = new AuthResult("a", "Bearer", 999L, new AuthUser("i", "e", "n"));

    RefreshSessionResponse response = sut.toResponse(result);

    assertEquals(999L, response.expiresIn());
  }

  @Test
  void toResponse_should_map_result_user() {
    AuthUser user = new AuthUser("id-1", "mail@test.com", "Full Name");
    AuthResult result = new AuthResult("a", "Bearer", 60L, user);

    RefreshSessionResponse response = sut.toResponse(result);

    assertEquals(user, response.user());
  }
}
