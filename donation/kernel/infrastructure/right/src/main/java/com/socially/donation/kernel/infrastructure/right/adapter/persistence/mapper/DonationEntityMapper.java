package com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper;

import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import org.springframework.stereotype.Component;

@Component
public final class DonationEntityMapper {

  public DonationEntity toEntity(Donation donation) {
    return DonationEntity.create(
        donation.id().value(),
        donation.title().value(),
        donation.description().value(),
        donation.createdAt(),
        donation.lastUpdatedAt());
  }

  public Donation toDomain(DonationEntity entity) {
    return Donation.create(
        Id.from(entity.getId().toString()),
        Title.from(entity.getTitle()),
        Description.from(entity.getDescription()),
        entity.getCreatedAt(),
        entity.getLastUpdatedAt());
  }
}
