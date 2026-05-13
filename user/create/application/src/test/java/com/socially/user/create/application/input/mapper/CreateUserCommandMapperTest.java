package com.socially.user.create.application.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.user.create.application.input.CreateUserCommand;
import com.socially.user.kernel.domain.entity.User;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CreateUserCommandMapperTest {

  private final CreateUserCommandMapper mapper = new CreateUserCommandMapper();

  private static final Instant NOW = Instant.parse("2024-06-01T12:00:00Z");

  @Test
  void toDomain_should_map_command_email() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");

    User actual = mapper.toDomain(command, NOW);

    assertEquals("user@example.com", actual.email().value());
  }

  @Test
  void toDomain_should_map_command_given_name() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");

    User actual = mapper.toDomain(command, NOW);

    assertEquals("Jane", actual.givenName());
  }

  @Test
  void toDomain_should_map_command_family_name() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");

    User actual = mapper.toDomain(command, NOW);

    assertEquals("Doe", actual.familyName());
  }

  @Test
  void toDomain_should_map_created_at_from_received_now() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");

    User actual = mapper.toDomain(command, NOW);

    assertEquals(NOW, actual.createdAt());
  }

  @Test
  void toDomain_should_generate_id() {
    var command = new CreateUserCommand("user@example.com", "Jane", "Doe");

    User actual = mapper.toDomain(command, NOW);

    org.junit.jupiter.api.Assertions.assertNotNull(actual.id());
    org.junit.jupiter.api.Assertions.assertNotNull(actual.id().value());
  }
}
