package com.socially.user.findbyfederatedidentity.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.FederatedIdentityEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.FederatedIdentityEntity;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper.UserEntityMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaFindUserByFederatedIdentityRepositoryTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private FederatedIdentityEntityRepository federatedIdentityEntityRepository;
  @Mock private UserEntityRepository userEntityRepository;
  @Mock private UserEntityMapper userEntityMapper;

  @InjectMocks private JpaFindUserByFederatedIdentityRepository repository;

  @Test
  void findByIssuerAndSubject_should_call_federated_identity_repository_with_issuer_and_subject() {
    when(federatedIdentityEntityRepository.findByIssuerAndSubject("https://idp.example", "sub-1"))
        .thenReturn(Optional.empty());

    repository.findByIssuerAndSubject("https://idp.example", "sub-1");

    verify(federatedIdentityEntityRepository)
        .findByIssuerAndSubject("https://idp.example", "sub-1");
  }

  @Test
  void findByIssuerAndSubject_should_return_empty_when_federated_identity_not_found() {
    when(federatedIdentityEntityRepository.findByIssuerAndSubject("https://idp.example", "sub-1"))
        .thenReturn(Optional.empty());

    Optional<User> result = repository.findByIssuerAndSubject("https://idp.example", "sub-1");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByIssuerAndSubject_should_call_user_repository_with_user_id_from_federated_identity() {
    UUID userUuid = UUID.fromString(USER_ID);
    FederatedIdentityEntity federatedIdentity =
        FederatedIdentityEntity.create(
            UUID.randomUUID(),
            userUuid,
            "https://idp.example",
            "sub-1",
            "user@example.com",
            CREATED_AT);
    UserEntity userEntity =
        UserEntity.create(userUuid, "user@example.com", CREATED_AT)
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    when(federatedIdentityEntityRepository.findByIssuerAndSubject("https://idp.example", "sub-1"))
        .thenReturn(Optional.of(federatedIdentity));
    when(userEntityRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));
    when(userEntityMapper.toDomain(userEntity))
        .thenReturn(
            User.create(Id.from(USER_ID), Email.from("user@example.com"), CREATED_AT)
                .withGivenName(Optional.of("Jane"))
                .withFamilyName(Optional.of("Doe")));

    repository.findByIssuerAndSubject("https://idp.example", "sub-1");

    verify(userEntityRepository).findById(userUuid);
  }

  @Test
  void findByIssuerAndSubject_should_return_mapped_domain_user_when_user_exists() {
    UUID userUuid = UUID.fromString(USER_ID);
    FederatedIdentityEntity federatedIdentity =
        FederatedIdentityEntity.create(
            UUID.randomUUID(),
            userUuid,
            "https://idp.example",
            "sub-1",
            "user@example.com",
            CREATED_AT);
    UserEntity userEntity =
        UserEntity.create(userUuid, "user@example.com", CREATED_AT)
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    User mappedUser =
        User.create(Id.from(USER_ID), Email.from("user@example.com"), CREATED_AT)
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    when(federatedIdentityEntityRepository.findByIssuerAndSubject("https://idp.example", "sub-1"))
        .thenReturn(Optional.of(federatedIdentity));
    when(userEntityRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));
    when(userEntityMapper.toDomain(userEntity)).thenReturn(mappedUser);

    Optional<User> result = repository.findByIssuerAndSubject("https://idp.example", "sub-1");

    assertEquals(Optional.of(mappedUser), result);
  }
}
