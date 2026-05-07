package com.socially.auth.me.infrastructure.left.adapter.http.me.output.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.me.infrastructure.left.adapter.http.me.output.AuthUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthUserResponseMapperTest {

  private AuthUserResponseMapper sut;

  @BeforeEach
  void setUp() {
    sut = new AuthUserResponseMapper();
  }

  @Test
  void toResponse_should_map_id() {
    String id = "user-123";
    AuthUser authUser = new AuthUser(id, null, null);

    AuthUserResponse response = sut.toResponse(authUser);

    assertEquals(id, response.id());
  }

  @Test
  void toResponse_should_map_email() {
    String email = "test@example.com";
    AuthUser authUser = new AuthUser(null, email, null);

    AuthUserResponse response = sut.toResponse(authUser);

    assertEquals(email, response.email());
  }

  @Test
  void toResponse_should_map_name() {
    String name = "Test User";
    AuthUser authUser = new AuthUser(null, null, name);

    AuthUserResponse response = sut.toResponse(authUser);

    assertEquals(name, response.name());
  }
}
