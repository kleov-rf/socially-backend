package com.socially.donation.getbyid.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaFindDonationByIdRepositoryTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private DonationEntityRepository entityRepository;
  @Mock private DonationEntityMapper entityMapper;

  @InjectMocks private JpaFindDonationByIdRepository sut;

  @Test
  void findById_should_call_entity_repository_find_with_id() {
    Id donationId = Id.from(DONATION_ID);
    when(entityRepository.findByIdAndDeletedAtIsNull(donationId.value()))
        .thenReturn(Optional.empty());

    sut.findById(donationId);

    verify(entityRepository).findByIdAndDeletedAtIsNull(donationId.value());
  }

  @Test
  void findById_should_return_mapped_donation() {
    var entityId = Id.from(DONATION_ID).value();
    DonationEntity entity =
        DonationEntity.create(
            entityId,
            Id.from(DONOR_ID).value(),
            "Entity Title",
            "Entity Description",
            CREATED_AT,
            CREATED_AT);
    when(entityRepository.findByIdAndDeletedAtIsNull(entityId)).thenReturn(Optional.of(entity));
    Donation mappedDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Entity Title"),
            Description.from("Entity Description"),
            CREATED_AT,
            CREATED_AT);
    when(entityMapper.toDomain(entity)).thenReturn(mappedDonation);

    Optional<Donation> result = sut.findById(Id.from(DONATION_ID));

    assertTrue(result.isPresent());
    assertEquals(mappedDonation, result.get());
  }
}
