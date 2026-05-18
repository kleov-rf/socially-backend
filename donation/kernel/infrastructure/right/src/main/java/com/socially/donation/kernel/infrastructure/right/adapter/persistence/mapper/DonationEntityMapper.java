package com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import org.springframework.stereotype.Component;

@Component
public final class DonationEntityMapper {

  public DonationEntity toEntity(Donation donation) {
    DonationLocation location = donation.location();
    return DonationEntity.create(
        donation.id().value(),
        donation.donorId().value(),
        donation.title().value(),
        donation.description().value(),
        donation.createdAt(),
        donation.lastUpdatedAt(),
        location != null ? location.address() : null,
        location != null ? location.latitude() : null,
        location != null ? location.longitude() : null);
  }

  public Donation toDomain(DonationEntity entity) {
    return Donation.create(
        Id.from(entity.getId().toString()),
        Id.from(entity.getDonorId().toString()),
        Title.from(entity.getTitle()),
        Description.from(entity.getDescription()),
        entity.getCreatedAt(),
        entity.getLastUpdatedAt(),
        toDonationLocation(entity));
  }

  private static DonationLocation toDonationLocation(DonationEntity entity) {
    if (entity.getLocationAddress() == null
        || entity.getLocationLatitude() == null
        || entity.getLocationLongitude() == null) {
      return null;
    }

    return DonationLocation.from(
        entity.getLocationAddress(), entity.getLocationLatitude(), entity.getLocationLongitude());
  }
}
