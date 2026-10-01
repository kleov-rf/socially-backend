package com.socially.user.create.application.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.kernel.domain.entity.User;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CreateUserCommandMapperTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant NOW = Instant.parse("2024-06-01T12:00:00Z");

  private final CreateUserCommandMapper mapper = new CreateUserCommandMapper();

  @Test
  void toDomain_should_map_user_id_from_command() {
    var command =
        CreateUserCommand.create(USER_ID, "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    User actual = mapper.toDomain(command, NOW);

    assertEquals(USER_ID, actual.id().value().toString());
  }

  @Test
  void toDomain_should_map_command_email() {
    var command =
        CreateUserCommand.create(USER_ID, "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    User actual = mapper.toDomain(command, NOW);

    assertEquals("user@example.com", actual.email().value());
  }

  @Test
  void toDomain_should_map_command_given_name() {
    var command =
        CreateUserCommand.create(USER_ID, "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    User actual = mapper.toDomain(command, NOW);

    assertEquals(Optional.of("Jane"), actual.givenName());
  }

  @Test
  void toDomain_should_map_command_family_name() {
    var command =
        CreateUserCommand.create(USER_ID, "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    User actual = mapper.toDomain(command, NOW);

    assertEquals(Optional.of("Doe"), actual.familyName());
  }

  @Test
  void toDomain_should_map_created_at_from_received_now() {
    var command =
        CreateUserCommand.create(USER_ID, "user@example.com")
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    User actual = mapper.toDomain(command, NOW);

    assertEquals(NOW, actual.createdAt());
  }
}
