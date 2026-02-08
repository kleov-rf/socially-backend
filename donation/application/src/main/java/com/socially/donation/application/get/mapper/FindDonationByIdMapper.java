package com.socially.donation.application.get.mapper;

import com.socially.donation.application.get.output.DonationDto;
import com.socially.donation.domain.entity.Donation;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class FindDonationByIdMapper {
  public static DonationDto toQueryResult(Donation donation) {
    return new DonationDto(donation.id(), donation.title(), donation.description());
  }
}
