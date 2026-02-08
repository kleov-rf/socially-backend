package com.socially.donation.infrastructure.left.adapter.http.get.mapper;

import com.socially.donation.application.get.output.DonationDto;
import com.socially.donation.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DonationResponseMapper {
  public DonationResponseDto toResponse(DonationDto result) {
    return new DonationResponseDto(
        result.id().value().toString(), result.title().value(), result.description().value());
  }
}
