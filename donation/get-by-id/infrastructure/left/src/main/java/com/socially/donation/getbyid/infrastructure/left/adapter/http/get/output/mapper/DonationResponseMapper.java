package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper;

import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationLocationResponseDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DonationResponseMapper {

  private final DonationImageResponseMapper donationImageResponseMapper;

  public DonationResponseDto toResponse(DonationDto result) {
    return new DonationResponseDto(
        result.id().value().toString(),
        result.title().value(),
        result.description().value(),
        new DonationLocationResponseDto(
            result.location().address(),
            result.location().latitude(),
            result.location().longitude()),
        result.createdAt(),
        result.lastUpdatedAt(),
        new DonorResponseDto(
            result.donor().id(),
            result.donor().email(),
            result.donor().givenName(),
            result.donor().familyName()),
        donationImageResponseMapper.toResponses(result.images()));
  }
}
