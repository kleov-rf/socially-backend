package com.socially.donation.delete.infrastructure.right.adapter.persistence;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaDeleteDonationRepositoryTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant NOW = Instant.parse("2026-04-22T12:00:00Z");

  @Mock private DonationEntityRepository entityRepository;
  @Mock private Clock clock;

  @InjectMocks private JpaDeleteDonationRepository sut;

  @Test
  void deleteById_should_call_entity_repository_soft_delete_with_id_and_current_time() {
    Id donationId = Id.from(DONATION_ID);
    when(clock.instant()).thenReturn(NOW);

    sut.deleteById(donationId);

    verify(entityRepository).softDeleteById(donationId.value(), NOW);
  }
}
