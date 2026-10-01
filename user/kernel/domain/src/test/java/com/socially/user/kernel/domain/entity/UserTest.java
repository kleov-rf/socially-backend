package com.socially.user.kernel.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.valueobject.Email;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserTest {
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String OTHER_USER_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Test
  void create_should_set_id() {
    User user = sampleUser(USER_ID);

    assertEquals(USER_ID, user.id().value().toString());
  }

  @Test
  void create_should_set_email() {
    User user = sampleUser(USER_ID);

    assertEquals("user@example.com", user.email().value());
  }

  @Test
  void create_should_set_given_name() {
    User user = sampleUser(USER_ID);

    assertEquals(Optional.of("Jane"), user.givenName());
  }

  @Test
  void create_should_set_family_name() {
    User user = sampleUser(USER_ID);

    assertEquals(Optional.of("Doe"), user.familyName());
  }

  @Test
  void create_should_set_created_at() {
    User user = sampleUser(USER_ID);

    assertEquals(CREATED_AT, user.createdAt());
  }

  @Test
  void equals_should_return_true_when_ids_are_equal() {
    User first = sampleUser(USER_ID);
    User second =
        User.create(
                Id.from(USER_ID),
                Email.from("different@example.com"),
                Instant.parse("2024-06-02T12:00:00Z"))
            .withGivenName(Optional.of("Another"))
            .withFamilyName(Optional.of("Name"));

    assertEquals(first, second);
  }

  @Test
  void equals_should_return_false_when_ids_are_different() {
    User first = sampleUser(USER_ID);
    User second = sampleUser(OTHER_USER_ID);

    assertNotEquals(first, second);
  }

  private User sampleUser(String id) {
    return User.create(Id.from(id), Email.from("user@example.com"), CREATED_AT)
        .withGivenName(Optional.of("Jane"))
        .withFamilyName(Optional.of("Doe"));
  }
}
