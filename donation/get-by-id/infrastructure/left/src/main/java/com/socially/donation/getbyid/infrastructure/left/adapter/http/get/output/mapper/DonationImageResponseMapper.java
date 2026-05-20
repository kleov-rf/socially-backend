package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper;

import com.socially.donation.getbyid.application.output.DonationImageDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationImageResponseDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class DonationImageResponseMapper {

  public DonationImageResponseDto toResponse(DonationImageDto image) {
    return new DonationImageResponseDto(
        image.imageId(), image.mediaUrl(), image.contentType(), image.sizeBytes(), image.primary());
  }

  public List<DonationImageResponseDto> toResponses(List<DonationImageDto> images) {
    return images.stream().map(this::toResponse).toList();
  }
}
