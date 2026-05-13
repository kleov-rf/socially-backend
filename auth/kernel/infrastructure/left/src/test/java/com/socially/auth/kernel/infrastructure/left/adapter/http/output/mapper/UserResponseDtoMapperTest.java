package com.socially.auth.kernel.infrastructure.left.adapter.http.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.domain.valueobject.Id;
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
}
