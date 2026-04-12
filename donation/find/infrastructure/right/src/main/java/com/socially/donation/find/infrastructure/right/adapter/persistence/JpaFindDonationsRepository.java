package com.socially.donation.find.infrastructure.right.adapter.persistence;

import com.socially.donation.find.domain.port.right.FindDonationsRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaFindDonationsRepository implements FindDonationsRepository {

  private final DonationEntityRepository entityRepository;
  private final DonationEntityMapper entityMapper;

  @Override
  public List<Donation> find() {
    return entityRepository.findAll().stream().map(entityMapper::toDomain).toList();
  }
}
