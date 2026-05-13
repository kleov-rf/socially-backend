package com.socially.auth.kernel.infrastructure.right.adapter.user.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.socially.auth.kernel.domain.AuthUser;
import org.junit.jupiter.api.Test;

class AuthUserToCreateUserCommandMapperTest {

  private final AuthUserToCreateUserCommandMapper sut = new AuthUserToCreateUserCommandMapper();

  @Test
  void toCommand_should_map_email() {
    AuthUser authUser = new AuthUser("auth-sub-1", "john@example.com", "John Doe");

    var command = sut.toCommand(authUser);

    assertEquals("john@example.com", command.email());
  }

  @Test
  void toCommand_should_map_given_name() {
    AuthUser authUser = new AuthUser("auth-sub-1", "john@example.com", "John Doe");

    var command = sut.toCommand(authUser);

    assertEquals("John", command.givenName());
  }

  @Test
  void toCommand_should_map_family_name() {
    AuthUser authUser = new AuthUser("auth-sub-1", "john@example.com", "John Doe");

    var command = sut.toCommand(authUser);

    assertEquals("Doe", command.familyName());
  }

  @Test
  void toCommand_should_set_null_family_name_when_received_name_has_single_part() {
    AuthUser authUser = new AuthUser("auth-sub-1", "john@example.com", "John");

    var command = sut.toCommand(authUser);

    assertEquals("John", command.givenName());
    assertNull(command.familyName());
  }
}
