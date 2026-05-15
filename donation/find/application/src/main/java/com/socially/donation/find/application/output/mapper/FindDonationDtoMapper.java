package com.socially.donation.find.application.output.mapper;

import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.output.FindDonorSummaryDto;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donor.kernel.domain.entity.Donor;
import org.springframework.stereotype.Component;

@Component
public final class FindDonationDtoMapper {
  public FindDonationDto fromDomain(Donation donation, Donor donor) {
    return new FindDonationDto(
        donation.id(),
        donation.title(),
        donation.description(),
        donation.createdAt(),
        donation.lastUpdatedAt(),
        new FindDonorSummaryDto(
            donor.id().value().toString(), donor.givenName(), donor.familyName()));
  }
}
