package com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class UserResponseDtoMapperTest {

  private final UserResponseDtoMapper sut = new UserResponseDtoMapper();

  @Test
  void toResponse_should_map_user_id() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));

    var response = sut.toResponse(user);

    assertEquals("550e8400-e29b-41d4-a716-446655440000", response.id());
  }

  @Test
  void toResponse_should_map_user_email() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));

    var response = sut.toResponse(user);

    assertEquals("user@example.com", response.email());
  }

  @Test
  void toResponse_should_map_user_name() {
    User user =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));

    var response = sut.toResponse(user);

    assertEquals("Jane Doe", response.name());
  }

  @Test
  void toResponse_should_map_auth_user_subject() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-1", "auth@example.com", null, null);

    var response = sut.toResponse(authUser);

    assertEquals("auth-1", response.id());
  }

  @Test
  void toResponse_should_map_auth_user_email() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-1", "auth@example.com", null, null);

    var response = sut.toResponse(authUser);

    assertEquals("auth@example.com", response.email());
  }

  @Test
  void toResponse_should_map_auth_user_name_when_given_and_family_present() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-1", "auth@example.com", "Auth", "User");

    var response = sut.toResponse(authUser);

    assertEquals("Auth User", response.name());
  }

  @Test
  void toResponse_should_map_auth_user_name_when_only_given_name_present() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-1", "auth@example.com", "Auth", null);

    var response = sut.toResponse(authUser);

    assertEquals("Auth", response.name());
  }

  @Test
  void toResponse_should_map_auth_user_name_when_only_family_name_present() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-1", "auth@example.com", null, "User");

    var response = sut.toResponse(authUser);

    assertEquals("User", response.name());
  }

  @Test
  void toResponse_should_map_null_name_when_auth_user_given_and_family_are_blank() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-1", "auth@example.com", null, null);

    var response = sut.toResponse(authUser);

    assertNull(response.name());
  }
}
