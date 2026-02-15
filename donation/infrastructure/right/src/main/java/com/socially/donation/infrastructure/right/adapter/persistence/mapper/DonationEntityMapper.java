package com.socially.donation.infrastructure.right.adapter.persistence.mapper;

import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import com.socially.donation.infrastructure.right.adapter.persistence.entity.DonationEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class DonationEntityMapper {

  public static DonationEntity toEntity(Donation donation) {
    return new DonationEntity(
        donation.id().value(), donation.title().value(), donation.description().value());
  }

  public static Donation toDomain(DonationEntity entity) {
    return Donation.create(
        Id.from(entity.getId().toString()),
        Title.from(entity.getTitle()),
        Description.from(entity.getDescription()));
  }
}
