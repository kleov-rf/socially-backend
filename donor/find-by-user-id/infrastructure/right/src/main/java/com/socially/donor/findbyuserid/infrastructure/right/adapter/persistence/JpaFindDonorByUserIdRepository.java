package com.socially.donor.findbyuserid.infrastructure.right.adapter.persistence;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyuserid.domain.port.right.FindDonorByUserIdRepository;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.DonorEntityRepository;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.mapper.DonorEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaFindDonorByUserIdRepository implements FindDonorByUserIdRepository {

  private final DonorEntityRepository entityRepository;
  private final DonorEntityMapper entityMapper;

  @Override
  public Optional<Donor> findByUserId(Id userId) {
    return entityRepository.findByUserId(userId.value()).map(entityMapper::toDomain);
  }
}
