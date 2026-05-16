package com.socially.user.federatedidentity.link.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.user.kernel.infrastructure.right.adapter.persistence.FederatedIdentityEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.FederatedIdentityEntity;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaLinkFederatedIdentityRepositoryTest {

  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private FederatedIdentityEntityRepository federatedIdentityEntityRepository;
  @Mock private Clock clock;

  @InjectMocks private JpaLinkFederatedIdentityRepository repository;

  @Test
  void link_should_call_federated_identity_repository_save() {
    when(clock.instant()).thenReturn(CREATED_AT);

    repository.link(USER_ID, "https://idp.example", "sub-1", "user@example.com");

    ArgumentCaptor<FederatedIdentityEntity> entityCaptor =
        ArgumentCaptor.forClass(FederatedIdentityEntity.class);
    verify(federatedIdentityEntityRepository).save(entityCaptor.capture());
    assertNotNull(entityCaptor.getValue());
  }

  @Test
  void link_should_use_clock_instant_for_created_at() {
    when(clock.instant()).thenReturn(CREATED_AT);

    repository.link(USER_ID, "https://idp.example", "sub-1", "user@example.com");

    ArgumentCaptor<FederatedIdentityEntity> entityCaptor =
        ArgumentCaptor.forClass(FederatedIdentityEntity.class);
    verify(federatedIdentityEntityRepository).save(entityCaptor.capture());
    assertEquals(CREATED_AT, entityCaptor.getValue().getCreatedAt());
  }

  @Test
  void link_should_map_user_id_from_received_value() {
    when(clock.instant()).thenReturn(CREATED_AT);

    repository.link(USER_ID, "https://idp.example", "sub-1", "user@example.com");

    ArgumentCaptor<FederatedIdentityEntity> entityCaptor =
        ArgumentCaptor.forClass(FederatedIdentityEntity.class);
    verify(federatedIdentityEntityRepository).save(entityCaptor.capture());
    assertEquals(UUID.fromString(USER_ID), entityCaptor.getValue().getUserId());
  }

  @Test
  void link_should_map_issuer_from_received_value() {
    when(clock.instant()).thenReturn(CREATED_AT);

    repository.link(USER_ID, "https://idp.example", "sub-1", "user@example.com");

    ArgumentCaptor<FederatedIdentityEntity> entityCaptor =
        ArgumentCaptor.forClass(FederatedIdentityEntity.class);
    verify(federatedIdentityEntityRepository).save(entityCaptor.capture());
    assertEquals("https://idp.example", entityCaptor.getValue().getIssuer());
  }

  @Test
  void link_should_map_subject_from_received_value() {
    when(clock.instant()).thenReturn(CREATED_AT);

    repository.link(USER_ID, "https://idp.example", "sub-1", "user@example.com");

    ArgumentCaptor<FederatedIdentityEntity> entityCaptor =
        ArgumentCaptor.forClass(FederatedIdentityEntity.class);
    verify(federatedIdentityEntityRepository).save(entityCaptor.capture());
    assertEquals("sub-1", entityCaptor.getValue().getSubject());
  }

  @Test
  void link_should_map_email_from_received_value() {
    when(clock.instant()).thenReturn(CREATED_AT);

    repository.link(USER_ID, "https://idp.example", "sub-1", "user@example.com");

    ArgumentCaptor<FederatedIdentityEntity> entityCaptor =
        ArgumentCaptor.forClass(FederatedIdentityEntity.class);
    verify(federatedIdentityEntityRepository).save(entityCaptor.capture());
    assertEquals("user@example.com", entityCaptor.getValue().getEmail());
  }
}
