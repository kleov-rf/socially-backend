package com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationImageEntity;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class DonationEntityMapper {

  private final DonationImageEntityMapper donationImageEntityMapper;

  public DonationEntity toEntity(Donation donation) {
    DonationLocation location = donation.location();
    DonationEntity entity =
        DonationEntity.create(
            donation.id().value(),
            donation.donorId().value(),
            donation.title().value(),
            donation.description().value(),
            donation.createdAt(),
            donation.lastUpdatedAt(),
            location.address(),
            location.latitude(),
            location.longitude());
    syncImages(entity, donation.images());
    return entity;
  }

  public Donation toDomain(DonationEntity entity) {
    List<DonationImageEntity> imageEntities = entity.getImages();
    List<DonationImage> images =
        Objects.nonNull(imageEntities) && Hibernate.isInitialized(imageEntities)
            ? imageEntities.stream().map(donationImageEntityMapper::toDomain).toList()
            : List.of();

    return Donation.create(
        Id.from(entity.getId().toString()),
        Id.from(entity.getDonorId().toString()),
        Title.from(entity.getTitle()),
        Description.from(entity.getDescription()),
        DonationLocation.from(
            entity.getLocationAddress(),
            entity.getLocationLatitude(),
            entity.getLocationLongitude()),
        entity.getCreatedAt(),
        entity.getLastUpdatedAt(),
        images);
  }

  private void syncImages(DonationEntity entity, List<DonationImage> images) {
    entity.getImages().clear();
    for (DonationImage image : images) {
      DonationImageEntity imageEntity = donationImageEntityMapper.toEntity(image);
      imageEntity.attachTo(entity);
      entity.getImages().add(imageEntity);
    }
  }
}
