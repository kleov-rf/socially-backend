package com.socially.donation.update.infrastructure.right.adapter.persistence;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaUpdateDonationRepositoryTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private DonationEntityRepository entityRepository;
  @Mock private DonationEntityMapper entityMapper;

  @InjectMocks private JpaUpdateDonationRepository sut;

  @Test
  void update_should_call_entity_repository_save_with_mapped_donation_entity() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Updated Title"),
            Description.from("Updated Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            CREATED_AT);
    DonationEntity mappedEntity =
        DonationEntity.create(
            Id.from(DONATION_ID).value(),
            Id.from(DONOR_ID).value(),
            "Updated Title",
            "Updated Description",
            CREATED_AT,
            CREATED_AT,
            "Calle Mayor 1, Madrid",
            40.4168,
            -3.7038);
    when(entityMapper.toEntity(donation)).thenReturn(mappedEntity);

    sut.update(donation);

    verify(entityRepository)
        .save(
            argThat(
                entity ->
                    entity.getId().equals(mappedEntity.getId())
                        && entity.getDonorId().equals(mappedEntity.getDonorId())
                        && entity.getTitle().equals(mappedEntity.getTitle())
                        && entity.getDescription().equals(mappedEntity.getDescription())
                        && entity.getCreatedAt().equals(mappedEntity.getCreatedAt())
                        && entity.getLastUpdatedAt().equals(mappedEntity.getLastUpdatedAt())));
  }
}
