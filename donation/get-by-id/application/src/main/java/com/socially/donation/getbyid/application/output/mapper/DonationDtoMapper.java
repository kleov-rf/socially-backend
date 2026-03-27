package com.socially.donation.getbyid.application.output.mapper;

import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.kernel.domain.entity.Donation;
import org.springframework.stereotype.Component;

@Component
public final class DonationDtoMapper {
  public DonationDto fromDomain(Donation donation) {
    return new DonationDto(donation.id(), donation.title(), donation.description());
  }
}
