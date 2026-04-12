package com.socially.donation.find.application.output.mapper;

import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.kernel.domain.entity.Donation;
import org.springframework.stereotype.Component;

@Component
public final class FindDonationDtoMapper {
  public FindDonationDto fromDomain(Donation donation) {
    return new FindDonationDto(
        donation.id(),
        donation.title(),
        donation.description(),
        donation.createdAt(),
        donation.lastUpdatedAt());
  }
}
