package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper;

import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import org.springframework.stereotype.Component;

@Component
public class DonationResponseMapper {
  public DonationResponseDto toResponse(DonationDto result) {
    return new DonationResponseDto(
        result.id().value().toString(), result.title().value(), result.description().value());
  }
}
