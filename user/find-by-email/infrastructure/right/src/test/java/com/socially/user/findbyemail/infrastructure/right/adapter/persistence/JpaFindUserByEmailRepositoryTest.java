package com.socially.user.findbyemail.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper.UserEntityMapper;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaFindUserByEmailRepositoryTest {

  @Mock private UserEntityRepository entityRepository;
  @Mock private UserEntityMapper entityMapper;

  @InjectMocks private JpaFindUserByEmailRepository repository;

  @Test
  void findByEmail_should_call_entity_repository_with_email_value() {
    Email email = Email.from("user@example.com");
    when(entityRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

    repository.findByEmail(email);

    verify(entityRepository).findByEmail("user@example.com");
  }

  @Test
  void findByEmail_should_call_entity_mapper_with_retrieved_entity_when_found() {
    Email email = Email.from("user@example.com");
    UserEntity userEntity =
        UserEntity.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000").value(),
            "user@example.com",
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));
    User mappedUser =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));
    when(entityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));
    when(entityMapper.toDomain(userEntity)).thenReturn(mappedUser);

    repository.findByEmail(email);

    verify(entityMapper).toDomain(userEntity);
  }

  @Test
  void findByEmail_should_return_mapped_domain_user_when_entity_found() {
    Email email = Email.from("user@example.com");
    UserEntity userEntity =
        UserEntity.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000").value(),
            "user@example.com",
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));
    User mappedUser =
        User.create(
            Id.from("550e8400-e29b-41d4-a716-446655440000"),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            Instant.parse("2024-06-01T12:00:00Z"));
    when(entityRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userEntity));
    when(entityMapper.toDomain(userEntity)).thenReturn(mappedUser);

    Optional<User> result = repository.findByEmail(email);

    assertEquals(Optional.of(mappedUser), result);
  }

  @Test
  void findByEmail_should_return_empty_when_entity_repository_returns_empty() {
    Email email = Email.from("user@example.com");
    when(entityRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

    Optional<User> result = repository.findByEmail(email);

    assertTrue(result.isEmpty());
  }
}
