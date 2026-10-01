package com.socially.auth.kernel.infrastructure.right.adapter.user.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.socially.auth.kernel.domain.AuthUser;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthUserToCreateUserCommandMapperTest {

  private final AuthUserToCreateUserCommandMapper sut = new AuthUserToCreateUserCommandMapper();

  @Test
  void toCommand_should_set_user_id() {
    AuthUser authUser =
        AuthUser.create("https://idp.example", "auth-sub-1", "john@example.com")
            .withGivenName(Optional.of("John"))
            .withFamilyName(Optional.of("Doe"));

    var command = sut.toCommand(authUser);

    assertNotNull(command.userId());
  }

  @Test
  void toCommand_should_map_email() {
    AuthUser authUser =
        AuthUser.create("https://idp.example", "auth-sub-1", "john@example.com")
            .withGivenName(Optional.of("John"))
            .withFamilyName(Optional.of("Doe"));

    var command = sut.toCommand(authUser);

    assertEquals("john@example.com", command.email());
  }

  @Test
  void toCommand_should_map_given_name() {
    AuthUser authUser =
        AuthUser.create("https://idp.example", "auth-sub-1", "john@example.com")
            .withGivenName(Optional.of("John"))
            .withFamilyName(Optional.of("Doe"));

    var command = sut.toCommand(authUser);

    assertEquals(Optional.of("John"), command.givenName());
  }

  @Test
  void toCommand_should_map_family_name() {
    AuthUser authUser =
        AuthUser.create("https://idp.example", "auth-sub-1", "john@example.com")
            .withGivenName(Optional.of("John"))
            .withFamilyName(Optional.of("Doe"));

    var command = sut.toCommand(authUser);

    assertEquals(Optional.of("Doe"), command.familyName());
  }
}
