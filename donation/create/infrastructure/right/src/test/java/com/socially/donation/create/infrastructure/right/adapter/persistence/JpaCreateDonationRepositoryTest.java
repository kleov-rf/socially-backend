package com.socially.donation.create.infrastructure.right.adapter.persistence;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaCreateDonationRepositoryTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private DonationEntityRepository entityRepository;
  @Mock private DonationEntityMapper entityMapper;

  @InjectMocks private JpaCreateDonationRepository sut;

  @Test
  void create_should_call_entity_repository_save_with_mapped_donation_entity() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));
    DonationEntity mappedEntity =
        new DonationEntity(Id.from(DONATION_ID).value(), "Test Title", "Test Description");
    when(entityMapper.toEntity(donation)).thenReturn(mappedEntity);

    sut.create(donation);

    verify(entityRepository)
        .save(
            argThat(
                entity ->
                    entity.getId().equals(mappedEntity.getId())
                        && entity.getTitle().equals(mappedEntity.getTitle())
                        && entity.getDescription().equals(mappedEntity.getDescription())));
  }
}
