package com.socially.donor.kernel.infrastructure.right.adapter.persistence.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.infrastructure.right.adapter.persistence.entity.DonorEntity;
import org.springframework.stereotype.Component;

@Component
public final class DonorEntityMapper {

  public DonorEntity toEntity(Donor donor) {
    return DonorEntity.create(
            donor.id().value(), donor.userId().value(), donor.email(), donor.createdAt())
        .withGivenName(donor.givenName())
        .withFamilyName(donor.familyName());
  }

  public Donor toDomain(DonorEntity entity) {
    return Donor.create(
            Id.from(entity.getId().toString()),
            Id.from(entity.getUserId().toString()),
            entity.getEmail(),
            entity.getCreatedAt())
        .withGivenName(entity.givenName())
        .withFamilyName(entity.familyName());
  }
}
