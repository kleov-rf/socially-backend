package com.socially.donation.delete.infrastructure.right.adapter.persistence;

import static org.mockito.Mockito.verify;

import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaDeleteDonationRepositoryTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private DonationEntityRepository entityRepository;

  @InjectMocks private JpaDeleteDonationRepository sut;

  @Test
  void deleteById_should_call_entity_repository_delete_with_id() {
    Id donationId = Id.from(DONATION_ID);

    sut.deleteById(donationId);

    verify(entityRepository).deleteById(donationId.value());
  }
}
