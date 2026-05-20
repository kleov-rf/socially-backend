package com.socially.donation.createimage.application.input.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class CreateDonationImageCommandMapper {

  public DonationImage toDonationImage(CreateDonationImageCommand command, Instant createdAt) {
    Id imageId = Id.generate();
    return DonationImage.create(
        imageId,
        StorageObjectKey.forDonationImage(
            Id.from(command.donationId()), imageId, command.originalFileName()),
        ContentType.from(command.contentType()),
        command.sizeBytes(),
        command.primary(),
        createdAt);
  }
}
