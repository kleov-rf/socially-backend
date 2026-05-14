package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper;

import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonorResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DonationResponseMapper {
  public DonationResponseDto toResponse(DonationDto result) {
    return new DonationResponseDto(
        result.id().value().toString(),
        result.title().value(),
        result.description().value(),
        result.createdAt(),
        result.lastUpdatedAt(),
        new DonorResponseDto(
            result.donor().id(),
            result.donor().email(),
            result.donor().givenName(),
            result.donor().familyName()));
  }
}
