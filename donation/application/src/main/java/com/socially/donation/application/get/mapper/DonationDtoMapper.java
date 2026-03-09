package com.socially.donation.application.get.mapper;

import com.socially.donation.application.get.output.DonationDto;
import com.socially.donation.domain.entity.Donation;
import org.springframework.stereotype.Component;

@Component
public final class DonationDtoMapper {
  public DonationDto fromDomain(Donation donation) {
    return new DonationDto(donation.id(), donation.title(), donation.description());
  }
}
