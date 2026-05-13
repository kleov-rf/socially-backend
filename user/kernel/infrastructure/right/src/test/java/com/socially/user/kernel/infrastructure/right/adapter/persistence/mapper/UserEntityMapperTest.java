package com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.domain.valueobject.Id;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserEntityMapperTest {

  @InjectMocks private UserEntityMapper userEntityMapper;

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final User USER =
      User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);

  private static final UserEntity ENTITY =
      UserEntity.create(
          UUID.fromString(USER_ID), "entity@example.com", "John", "Smith", CREATED_AT);

  @Test
  void toEntity_should_map_id() {
    UserEntity result = userEntityMapper.toEntity(USER);

    assertEquals(UUID.fromString(USER_ID), result.getId());
  }

  @Test
  void toEntity_should_map_email() {
    UserEntity result = userEntityMapper.toEntity(USER);

    assertEquals("user@example.com", result.getEmail());
  }

  @Test
  void toEntity_should_map_given_name() {
    UserEntity result = userEntityMapper.toEntity(USER);

    assertEquals("Jane", result.getGivenName());
  }

  @Test
  void toEntity_should_map_family_name() {
    UserEntity result = userEntityMapper.toEntity(USER);

    assertEquals("Doe", result.getFamilyName());
  }

  @Test
  void toEntity_should_map_created_at() {
    UserEntity result = userEntityMapper.toEntity(USER);

    assertEquals(CREATED_AT, result.getCreatedAt());
  }

  @Test
  void toDomain_should_map_id() {
    User result = userEntityMapper.toDomain(ENTITY);

    assertEquals(Id.from(USER_ID), result.id());
  }

  @Test
  void toDomain_should_map_email() {
    User result = userEntityMapper.toDomain(ENTITY);

    assertEquals(Email.from("entity@example.com"), result.email());
  }

  @Test
  void toDomain_should_map_given_name() {
    User result = userEntityMapper.toDomain(ENTITY);

    assertEquals("John", result.givenName());
  }

  @Test
  void toDomain_should_map_family_name() {
    User result = userEntityMapper.toDomain(ENTITY);

    assertEquals("Smith", result.familyName());
  }

  @Test
  void toDomain_should_map_created_at() {
    User result = userEntityMapper.toDomain(ENTITY);

    assertEquals(CREATED_AT, result.createdAt());
  }
}
