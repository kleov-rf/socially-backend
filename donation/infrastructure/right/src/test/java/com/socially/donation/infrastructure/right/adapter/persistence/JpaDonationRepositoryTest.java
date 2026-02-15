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

  @InjectMocks private JpaDonationRepository jpaDonationRepository;

  @Test
  void save_should_call_entity_repository_save_with_mapped_donation_entity() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));

    jpaDonationRepository.save(donation);

    DonationEntity expected = DonationEntityMapper.toEntity(donation);
    verify(entityRepository)
        .save(
            argThat(
                entity ->
                    entity.getId().equals(expected.getId())
                        && entity.getTitle().equals(expected.getTitle())
                        && entity.getDescription().equals(expected.getDescription())));
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

    Optional<Donation> result = jpaDonationRepository.findById(Id.from(DONATION_ID));

    Donation expected = DonationEntityMapper.toDomain(entity);
    assertTrue(result.isPresent());
    assertEquals(expected, result.get());
  }
}
