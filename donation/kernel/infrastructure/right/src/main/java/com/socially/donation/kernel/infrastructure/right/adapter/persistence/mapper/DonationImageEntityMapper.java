package com.socially.donation.kernel.infrastructure.right.adapter.persistence.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationImageEntity;
import org.springframework.stereotype.Component;

@Component
public final class DonationImageEntityMapper {

  public DonationImageEntity toEntity(DonationImage image) {
    return DonationImageEntity.create(
        image.id().value(),
        image.storageObjectKey().value(),
        image.contentType().value(),
        image.sizeBytes(),
        image.primary(),
        image.createdAt());
  }

  public DonationImage toDomain(DonationImageEntity entity) {
    return DonationImage.create(
        Id.from(entity.getId().toString()),
        StorageObjectKey.from(entity.getStorageObjectKey()),
        ContentType.from(entity.getContentType()),
        entity.getSizeBytes(),
        entity.getPrimaryFlag(),
        entity.getCreatedAt());
  }
}
