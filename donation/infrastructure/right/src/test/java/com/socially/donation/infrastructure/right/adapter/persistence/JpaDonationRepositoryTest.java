package com.socially.donation.infrastructure.right.adapter.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import com.socially.donation.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaDonationRepositoryTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private DonationEntityRepository entityRepository;

  @Mock private DonationEntityMapper entityMapper;

  @InjectMocks private JpaDonationRepository jpaDonationRepository;

  @Test
  void save_should_call_entity_repository_save_with_mapped_donation_entity() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));
    DonationEntity mappedEntity =
        new DonationEntity(Id.from(DONATION_ID).value(), "Test Title", "Test Description");
    when(entityMapper.toEntity(donation)).thenReturn(mappedEntity);

    jpaDonationRepository.save(donation);

    verify(entityRepository)
        .save(
            argThat(
                entity ->
                    entity.getId().equals(mappedEntity.getId())
                        && entity.getTitle().equals(mappedEntity.getTitle())
                        && entity.getDescription().equals(mappedEntity.getDescription())));
  }

  @Test
  void findById_should_call_entity_repository_find_with_id() {
    Id donationId = Id.from(DONATION_ID);
    when(entityRepository.findById(donationId.value())).thenReturn(Optional.empty());

    jpaDonationRepository.findById(donationId);

    verify(entityRepository).findById(donationId.value());
  }

  @Test
  void findById_should_return_mapped_donation() {
    var entityId = Id.from(DONATION_ID).value();
    DonationEntity entity = new DonationEntity(entityId, "Entity Title", "Entity Description");
    when(entityRepository.findById(entityId)).thenReturn(Optional.of(entity));
    Donation mappedDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Entity Title"),
            Description.from("Entity Description"));
    when(entityMapper.toDomain(entity)).thenReturn(mappedDonation);

    Optional<Donation> result = jpaDonationRepository.findById(Id.from(DONATION_ID));

    assertTrue(result.isPresent());
    assertEquals(mappedDonation, result.get());
  }
}
