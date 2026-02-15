package com.socially.donation.infrastructure.right.adapter.persistence;

import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.right.DonationRepository;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaDonationRepository implements DonationRepository {

  private final DonationEntityRepository entityRepository;

  public JpaDonationRepository(DonationEntityRepository entityRepository) {
    this.entityRepository = entityRepository;
  }

  @Override
  public void save(Donation donation) {
    entityRepository.save(DonationEntityMapper.toEntity(donation));
  }

  @Override
  public Optional<Donation> findById(Id id) {
    return entityRepository.findById(id.value()).map(DonationEntityMapper::toDomain);
  }
}
