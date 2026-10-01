package com.socially.user.create.infrastructure.right.adapter.persistence;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper.UserEntityMapper;
import java.time.Instant;
import java.util.Optional;
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
  void create_should_call_entity_mapper_with_received_user() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), CREATED_AT)
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));

    sut.create(user);

    verify(entityMapper).toEntity(user);
  }

  @Test
  void create_should_call_entity_repository_save_with_mapped_entity() {
    User user =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), CREATED_AT)
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    var mappedEntity =
        com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity.create(
                Id.from(USER_ID).value(), "user@example.com", CREATED_AT)
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    when(entityMapper.toEntity(user)).thenReturn(mappedEntity);
    when(entityRepository.save(mappedEntity)).thenReturn(mappedEntity);

    sut.create(user);

    verify(entityRepository).save(mappedEntity);
  }
}
