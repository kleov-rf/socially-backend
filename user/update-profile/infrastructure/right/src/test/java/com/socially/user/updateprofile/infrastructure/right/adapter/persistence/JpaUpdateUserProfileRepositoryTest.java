package com.socially.user.updateprofile.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.user.kernel.infrastructure.right.adapter.persistence.FederatedIdentityEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.FederatedIdentityEntity;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaUpdateUserProfileRepositoryTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private UserEntityRepository userEntityRepository;
  @Mock private FederatedIdentityEntityRepository federatedIdentityEntityRepository;

  @InjectMocks private JpaUpdateUserProfileRepository repository;

  @Test
  void updateProfile_should_load_user_by_id() {
    UUID userUuid = UUID.fromString(USER_ID);
    UserEntity userEntity =
        UserEntity.create(userUuid, "old@example.com", "Jane", "Doe", CREATED_AT);
    FederatedIdentityEntity federatedIdentity =
        FederatedIdentityEntity.create(
            UUID.randomUUID(),
            userUuid,
            "https://idp.example",
            "sub-1",
            "old@example.com",
            CREATED_AT);
    when(userEntityRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));
    when(federatedIdentityEntityRepository.findByIssuerAndSubject("https://idp.example", "sub-1"))
        .thenReturn(Optional.of(federatedIdentity));
    when(userEntityRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(userEntity);
    when(federatedIdentityEntityRepository.save(org.mockito.ArgumentMatchers.any()))
        .thenReturn(federatedIdentity);

    repository.updateProfile(
        USER_ID,
        "https://idp.example",
        "sub-1",
        Optional.of("new@example.com"),
        Optional.of("Janet"),
        Optional.of("Smith"));

    verify(userEntityRepository).findById(userUuid);
  }

  @Test
  void updateProfile_should_save_updated_user_entity_with_new_email() {
    UUID userUuid = UUID.fromString(USER_ID);
    UserEntity userEntity =
        UserEntity.create(userUuid, "old@example.com", "Jane", "Doe", CREATED_AT);
    FederatedIdentityEntity federatedIdentity =
        FederatedIdentityEntity.create(
            UUID.randomUUID(),
            userUuid,
            "https://idp.example",
            "sub-1",
            "old@example.com",
            CREATED_AT);
    when(userEntityRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));
    when(federatedIdentityEntityRepository.findByIssuerAndSubject("https://idp.example", "sub-1"))
        .thenReturn(Optional.of(federatedIdentity));
    when(userEntityRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(userEntity);
    when(federatedIdentityEntityRepository.save(org.mockito.ArgumentMatchers.any()))
        .thenReturn(federatedIdentity);

    repository.updateProfile(
        USER_ID,
        "https://idp.example",
        "sub-1",
        Optional.of("new@example.com"),
        Optional.empty(),
        Optional.empty());

    ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
    verify(userEntityRepository).save(userCaptor.capture());
    assertEquals("new@example.com", userCaptor.getValue().getEmail());
  }

  @Test
  void updateProfile_should_load_federated_identity_by_issuer_and_subject() {
    UUID userUuid = UUID.fromString(USER_ID);
    UserEntity userEntity =
        UserEntity.create(userUuid, "old@example.com", "Jane", "Doe", CREATED_AT);
    FederatedIdentityEntity federatedIdentity =
        FederatedIdentityEntity.create(
            UUID.randomUUID(),
            userUuid,
            "https://idp.example",
            "sub-1",
            "old@example.com",
            CREATED_AT);
    when(userEntityRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));
    when(federatedIdentityEntityRepository.findByIssuerAndSubject("https://idp.example", "sub-1"))
        .thenReturn(Optional.of(federatedIdentity));
    when(userEntityRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(userEntity);
    when(federatedIdentityEntityRepository.save(org.mockito.ArgumentMatchers.any()))
        .thenReturn(federatedIdentity);

    repository.updateProfile(
        USER_ID,
        "https://idp.example",
        "sub-1",
        Optional.of("new@example.com"),
        Optional.empty(),
        Optional.empty());

    verify(federatedIdentityEntityRepository)
        .findByIssuerAndSubject("https://idp.example", "sub-1");
  }

  @Test
  void updateProfile_should_save_updated_federated_identity_with_new_email() {
    UUID userUuid = UUID.fromString(USER_ID);
    UserEntity userEntity =
        UserEntity.create(userUuid, "old@example.com", "Jane", "Doe", CREATED_AT);
    FederatedIdentityEntity federatedIdentity =
        FederatedIdentityEntity.create(
            UUID.randomUUID(),
            userUuid,
            "https://idp.example",
            "sub-1",
            "old@example.com",
            CREATED_AT);
    when(userEntityRepository.findById(userUuid)).thenReturn(Optional.of(userEntity));
    when(federatedIdentityEntityRepository.findByIssuerAndSubject("https://idp.example", "sub-1"))
        .thenReturn(Optional.of(federatedIdentity));
    when(userEntityRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(userEntity);
    when(federatedIdentityEntityRepository.save(org.mockito.ArgumentMatchers.any()))
        .thenReturn(federatedIdentity);

    repository.updateProfile(
        USER_ID,
        "https://idp.example",
        "sub-1",
        Optional.of("new@example.com"),
        Optional.empty(),
        Optional.empty());

    ArgumentCaptor<FederatedIdentityEntity> federatedCaptor =
        ArgumentCaptor.forClass(FederatedIdentityEntity.class);
    verify(federatedIdentityEntityRepository).save(federatedCaptor.capture());
    assertEquals("new@example.com", federatedCaptor.getValue().getEmail());
  }
}
