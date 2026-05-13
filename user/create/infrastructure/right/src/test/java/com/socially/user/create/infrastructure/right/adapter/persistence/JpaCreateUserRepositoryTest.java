package com.socially.user.create.infrastructure.right.adapter.persistence;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.domain.valueobject.Id;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper.UserEntityMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaCreateUserRepositoryTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private UserEntityRepository entityRepository;
  @Mock private UserEntityMapper entityMapper;

  @InjectMocks private JpaCreateUserRepository sut;

  @Test
  void should_call_entity_mapper_with_received_user() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    UserEntity mappedEntity =
        UserEntity.create(Id.from(USER_ID).value(), "user@example.com", "Jane", "Doe", CREATED_AT);
    when(entityMapper.toEntity(user)).thenReturn(mappedEntity);
    when(entityRepository.save(mappedEntity)).thenReturn(mappedEntity);
    when(entityMapper.toDomain(mappedEntity)).thenReturn(user);

    sut.create(user);

    verify(entityMapper).toEntity(user);
  }

  @Test
  void should_call_entity_repository_with_mapped_user_entity() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    UserEntity mappedEntity =
        UserEntity.create(Id.from(USER_ID).value(), "user@example.com", "Jane", "Doe", CREATED_AT);
    when(entityMapper.toEntity(user)).thenReturn(mappedEntity);
    when(entityRepository.save(mappedEntity)).thenReturn(mappedEntity);
    when(entityMapper.toDomain(mappedEntity)).thenReturn(user);

    sut.create(user);

    verify(entityRepository).save(mappedEntity);
  }

  @Test
  void should_call_entity_mapper_with_retrieved_user_entity() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    UserEntity mappedEntity =
        UserEntity.create(Id.from(USER_ID).value(), "user@example.com", "Jane", "Doe", CREATED_AT);
    UserEntity savedEntity =
        UserEntity.create(
            Id.from("550e8400-e29b-41d4-a716-446655440001").value(),
            "user@example.com",
            "Jane",
            "Doe",
            CREATED_AT);
    when(entityMapper.toEntity(user)).thenReturn(mappedEntity);
    when(entityRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(entityMapper.toDomain(savedEntity)).thenReturn(user);

    sut.create(user);

    verify(entityMapper).toDomain(savedEntity);
  }

  @Test
  void should_return_mapped_user() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    User mappedUser =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440001"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            CREATED_AT);
    UserEntity mappedEntity =
        UserEntity.create(Id.from(USER_ID).value(), "user@example.com", "Jane", "Doe", CREATED_AT);
    UserEntity savedEntity =
        UserEntity.create(
            Id.from("550e8400-e29b-41d4-a716-446655440001").value(),
            "user@example.com",
            "Jane",
            "Doe",
            CREATED_AT);
    when(entityMapper.toEntity(user)).thenReturn(mappedEntity);
    when(entityRepository.save(mappedEntity)).thenReturn(savedEntity);
    when(entityMapper.toDomain(savedEntity)).thenReturn(mappedUser);

    User result = sut.create(user);

    org.junit.jupiter.api.Assertions.assertEquals(mappedUser, result);
  }
}
