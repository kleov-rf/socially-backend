package com.socially.user.create.infrastructure.right.adapter.persistence;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
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
  void create_should_check_if_user_exists_by_email() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(entityRepository.existsByEmail("user@example.com")).thenReturn(true);

    sut.create(user);

    verify(entityRepository).existsByEmail("user@example.com");
  }

  @Test
  void create_should_not_save_entity_when_user_email_already_exists() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(entityRepository.existsByEmail("user@example.com")).thenReturn(true);

    sut.create(user);

    verify(entityRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void create_should_call_entity_mapper_with_received_user_when_user_email_not_exists() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    when(entityRepository.existsByEmail("user@example.com")).thenReturn(false);

    sut.create(user);

    verify(entityMapper).toEntity(user);
  }

  @Test
  void create_should_call_entity_repository_save_with_mapped_entity_when_user_email_not_exists() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    var mappedEntity =
        com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity.create(
            Id.from(USER_ID).value(), "user@example.com", "Jane", "Doe", CREATED_AT);
    when(entityRepository.existsByEmail("user@example.com")).thenReturn(false);
    when(entityMapper.toEntity(user)).thenReturn(mappedEntity);
    when(entityRepository.save(mappedEntity)).thenReturn(mappedEntity);

    sut.create(user);

    verify(entityRepository).save(mappedEntity);
  }
}
