package com.socially.donor.create.infrastructure.right.adapter.persistence;

import com.socially.donor.create.domain.port.right.CreateDonorRepository;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.DonorEntityRepository;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.mapper.DonorEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCreateDonorRepository implements CreateDonorRepository {

  private final DonorEntityRepository entityRepository;
  private final DonorEntityMapper entityMapper;

  @Override
  public void create(Donor donor) {
    entityRepository.save(entityMapper.toEntity(donor));
  }
}
