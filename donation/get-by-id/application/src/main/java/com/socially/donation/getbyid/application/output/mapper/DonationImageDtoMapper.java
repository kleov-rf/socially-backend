package com.socially.donation.getbyid.application.output.mapper;

import com.socially.donation.getbyid.application.output.DonationImageDto;
import com.socially.donation.kernel.domain.entity.DonationImage;
import org.springframework.stereotype.Component;

@Component
public final class DonationImageDtoMapper {

  public DonationImageDto toDto(DonationImage image, String mediaUrl) {
    return new DonationImageDto(
        image.id().value().toString(),
        mediaUrl,
        image.contentType().value(),
        image.sizeBytes(),
        image.primary());
  }
}
