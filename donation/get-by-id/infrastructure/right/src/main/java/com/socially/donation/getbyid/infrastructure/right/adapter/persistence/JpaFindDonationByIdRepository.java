package com.socially.donation.getbyid.infrastructure.right.adapter.persistence;

import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.DonationEntityRepository;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper.DonationEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaFindDonationByIdRepository implements FindDonationByIdRepository {

  private final DonationEntityRepository entityRepository;
  private final DonationEntityMapper entityMapper;

  @Override
  public Optional<Donation> findById(Id id) {
    return entityRepository.findById(id.value()).map(entityMapper::toDomain);
  }
}
