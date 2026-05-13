package com.socially.donation.delete.infrastructure.right.adapter.persistence;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.delete.domain.port.right.DeleteDonationRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JpaDeleteDonationRepository implements DeleteDonationRepository {

  private final DonationEntityRepository entityRepository;
  private final Clock clock;

  @Override
  @Transactional
  public void deleteById(Id id) {
    entityRepository.softDeleteById(id.value(), clock.instant());
  }
}
