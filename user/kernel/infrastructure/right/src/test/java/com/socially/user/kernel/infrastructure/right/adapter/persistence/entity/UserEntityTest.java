package com.socially.user.kernel.infrastructure.right.adapter.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserEntityTest {

  private static final UUID ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
  private static final String EMAIL = "user@example.com";
  private static final String GIVEN_NAME = "Jane";
  private static final String FAMILY_NAME = "Doe";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Test
  void create_should_set_id() {
    UserEntity result = UserEntity.create(ID, EMAIL, GIVEN_NAME, FAMILY_NAME, CREATED_AT);

    assertEquals(ID, result.getId());
  }

  @Test
  void create_should_set_email() {
    UserEntity result = UserEntity.create(ID, EMAIL, GIVEN_NAME, FAMILY_NAME, CREATED_AT);

    assertEquals(EMAIL, result.getEmail());
  }

  @Test
  void create_should_set_given_name() {
    UserEntity result = UserEntity.create(ID, EMAIL, GIVEN_NAME, FAMILY_NAME, CREATED_AT);

    assertEquals(GIVEN_NAME, result.getGivenName());
  }

  @Test
  void create_should_set_family_name() {
    UserEntity result = UserEntity.create(ID, EMAIL, GIVEN_NAME, FAMILY_NAME, CREATED_AT);

    assertEquals(FAMILY_NAME, result.getFamilyName());
  }

  @Test
  void create_should_set_created_at() {
    UserEntity result = UserEntity.create(ID, EMAIL, GIVEN_NAME, FAMILY_NAME, CREATED_AT);

    assertEquals(CREATED_AT, result.getCreatedAt());
  }
}
