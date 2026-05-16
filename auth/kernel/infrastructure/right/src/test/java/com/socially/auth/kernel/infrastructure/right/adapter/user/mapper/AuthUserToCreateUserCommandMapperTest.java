package com.socially.auth.kernel.infrastructure.right.adapter.user.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.socially.auth.kernel.domain.AuthUser;
import org.junit.jupiter.api.Test;

class AuthUserToCreateUserCommandMapperTest {

  private final AuthUserToCreateUserCommandMapper sut = new AuthUserToCreateUserCommandMapper();

  @Test
  void toCommand_should_set_user_id() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-sub-1", "john@example.com", "John", "Doe");

    var command = sut.toCommand(authUser);

    assertNotNull(command.userId());
  }

  @Test
  void toCommand_should_map_email() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-sub-1", "john@example.com", "John", "Doe");

    var command = sut.toCommand(authUser);

    assertEquals("john@example.com", command.email());
  }

  @Test
  void toCommand_should_map_given_name() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-sub-1", "john@example.com", "John", "Doe");

    var command = sut.toCommand(authUser);

    assertEquals("John", command.givenName());
  }

  @Test
  void toCommand_should_map_family_name() {
    AuthUser authUser =
        new AuthUser("https://idp.example", "auth-sub-1", "john@example.com", "John", "Doe");

    var command = sut.toCommand(authUser);

    assertEquals("Doe", command.familyName());
  }
}
