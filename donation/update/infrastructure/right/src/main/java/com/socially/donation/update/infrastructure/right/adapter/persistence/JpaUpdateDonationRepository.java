package com.socially.donation.update.infrastructure.right.adapter.persistence;

import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import com.socially.donation.update.domain.port.right.UpdateDonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUpdateDonationRepository implements UpdateDonationRepository {

  private final DonationEntityRepository entityRepository;
  private final DonationEntityMapper entityMapper;

  @Override
  public void update(Donation donation) {
    entityRepository.save(entityMapper.toEntity(donation));
  }
}
