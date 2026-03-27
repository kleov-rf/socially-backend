package com.socially.donation.delete.infrastructure.right.adapter.persistence;

import com.socially.donation.delete.domain.port.right.DeleteDonationRepository;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDeleteDonationRepository implements DeleteDonationRepository {

  private final DonationEntityRepository entityRepository;

  @Override
  public void deleteById(Id id) {
    entityRepository.deleteById(id.value());
  }
}
