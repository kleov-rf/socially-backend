package com.socially.donation.create.infrastructure.right.adapter.persistence;

import com.socially.donation.create.domain.port.right.CreateDonationRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCreateDonationRepository implements CreateDonationRepository {

  private final DonationEntityRepository entityRepository;
  private final DonationEntityMapper entityMapper;

  @Override
  public void create(Donation donation) {
    entityRepository.save(entityMapper.toEntity(donation));
  }
}
