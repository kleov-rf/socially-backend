package com.socially.donation.getbyid.application.output.mapper;

import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.output.DonationLocationDto;
import com.socially.donation.getbyid.application.output.DonorSummaryDto;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donor.kernel.domain.entity.Donor;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class DonationDtoMapper {
  public DonationDto fromDomain(Donation donation, Donor donor) {
    DonationLocation location = donation.location();
    return new DonationDto(
        donation.id(),
        donation.title(),
        donation.description(),
        new DonationLocationDto(location.address(), location.latitude(), location.longitude()),
        donation.createdAt(),
        donation.lastUpdatedAt(),
        new DonorSummaryDto(
            donor.id().value().toString(), donor.email(), donor.givenName(), donor.familyName()),
        List.of());
  }
}
