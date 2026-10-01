package com.socially.donor.findbyuserid.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.DonorEntityRepository;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.entity.DonorEntity;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.mapper.DonorEntityMapper;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaFindDonorByUserIdRepositoryTest {

  @Mock private DonorEntityRepository entityRepository;
  @Mock private DonorEntityMapper entityMapper;

  @InjectMocks private JpaFindDonorByUserIdRepository repository;

  @Test
  void findByUserId_should_call_entity_repository_with_user_id_value() {
    Id userId = Id.from("550e8400-e29b-41d4-a716-446655440010");
    when(entityRepository.findByUserId(userId.value())).thenReturn(Optional.empty());

    repository.findByUserId(userId);

    verify(entityRepository).findByUserId(userId.value());
  }

  @Test
  void findByUserId_should_call_entity_mapper_with_retrieved_entity_when_found() {
    Id userId = Id.from("550e8400-e29b-41d4-a716-446655440010");
    DonorEntity donorEntity =
        DonorEntity.create(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                userId.value(),
                "user@example.com",
                Instant.parse("2024-06-01T12:00:00Z"))
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    Donor mappedDonor =
        Donor.create(
                Id.from("550e8400-e29b-41d4-a716-446655440001"),
                userId,
                "user@example.com",
                Instant.parse("2024-06-01T12:00:00Z"))
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    when(entityRepository.findByUserId(userId.value())).thenReturn(Optional.of(donorEntity));
    when(entityMapper.toDomain(donorEntity)).thenReturn(mappedDonor);

    repository.findByUserId(userId);

    verify(entityMapper).toDomain(donorEntity);
  }

  @Test
  void findByUserId_should_return_mapped_domain_donor_when_entity_found() {
    Id userId = Id.from("550e8400-e29b-41d4-a716-446655440010");
    DonorEntity donorEntity =
        DonorEntity.create(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                userId.value(),
                "user@example.com",
                Instant.parse("2024-06-01T12:00:00Z"))
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    Donor mappedDonor =
        Donor.create(
                Id.from("550e8400-e29b-41d4-a716-446655440001"),
                userId,
                "user@example.com",
                Instant.parse("2024-06-01T12:00:00Z"))
            .withGivenName(Optional.of("Jane"))
            .withFamilyName(Optional.of("Doe"));
    when(entityRepository.findByUserId(userId.value())).thenReturn(Optional.of(donorEntity));
    when(entityMapper.toDomain(donorEntity)).thenReturn(mappedDonor);

    Optional<Donor> result = repository.findByUserId(userId);

    assertEquals(Optional.of(mappedDonor), result);
  }

  @Test
  void findByUserId_should_return_empty_when_entity_repository_returns_empty() {
    Id userId = Id.from("550e8400-e29b-41d4-a716-446655440010");
    when(entityRepository.findByUserId(userId.value())).thenReturn(Optional.empty());

    Optional<Donor> result = repository.findByUserId(userId);

    assertTrue(result.isEmpty());
  }
}
