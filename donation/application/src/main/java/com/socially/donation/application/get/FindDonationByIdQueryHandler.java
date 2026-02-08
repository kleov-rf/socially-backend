package com.socially.donation.application.get;

import com.socially.donation.application.get.input.FindDonationByIdQuery;
import com.socially.donation.application.get.mapper.DonationDtoMapper;
import com.socially.donation.application.get.output.DonationDto;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.DonationRepository;
import com.socially.donation.domain.valueobject.Id;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FindDonationByIdQueryHandler {

  private final DonationRepository donationRepository;

  public Optional<DonationDto> handle(FindDonationByIdQuery query) {
    Id id = Id.from(query.id());
    Optional<Donation> donation = donationRepository.findById(id);
    return donation.map(DonationDtoMapper::fromDomain);
  }
}
