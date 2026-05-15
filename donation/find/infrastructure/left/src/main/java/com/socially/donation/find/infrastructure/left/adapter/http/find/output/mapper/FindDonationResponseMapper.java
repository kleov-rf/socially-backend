package com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper;

import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonorResponse;
import org.springframework.stereotype.Component;

@Component
public final class FindDonationResponseMapper {
  public FindDonationResponse toResponse(FindDonationDto result) {
    return new FindDonationResponse(
        result.id().value().toString(),
        result.title().value(),
        result.description().value(),
        result.createdAt(),
        result.lastUpdatedAt(),
        new FindDonorResponse(
            result.donor().id(), result.donor().givenName(), result.donor().familyName()));
  }
}
