package com.socially.donation.infrastructure.right.adapter.persistence;

import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.right.DonationRepository;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaDonationRepository implements DonationRepository {

  private final DonationEntityRepository entityRepository;
  private final DonationEntityMapper entityMapper;

  @Override
  public void save(Donation donation) {
    entityRepository.save(entityMapper.toEntity(donation));
  }

  @Override
  public Optional<Donation> findById(Id id) {
    return entityRepository.findById(id.value()).map(entityMapper::toDomain);
  }
}
