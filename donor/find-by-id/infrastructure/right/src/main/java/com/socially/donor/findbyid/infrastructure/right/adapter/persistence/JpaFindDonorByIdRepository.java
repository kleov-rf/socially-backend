package com.socially.donor.findbyid.infrastructure.right.adapter.persistence;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.findbyid.domain.port.right.FindDonorByIdRepository;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.DonorEntityRepository;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.mapper.DonorEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaFindDonorByIdRepository implements FindDonorByIdRepository {

  private final DonorEntityRepository entityRepository;
  private final DonorEntityMapper entityMapper;

  @Override
  public Optional<Donor> findById(Id donorId) {
    return entityRepository.findById(donorId.value()).map(entityMapper::toDomain);
  }
}
