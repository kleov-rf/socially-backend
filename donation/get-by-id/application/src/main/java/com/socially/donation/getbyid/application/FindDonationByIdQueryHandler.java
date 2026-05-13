package com.socially.donation.getbyid.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.input.FindDonationByIdQuery;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.output.mapper.DonationDtoMapper;
import com.socially.donation.getbyid.application.port.left.FindDonationByIdUseCase;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FindDonationByIdQueryHandler implements FindDonationByIdUseCase {

  private final FindDonationByIdRepository donationRepository;
  private final DonationDtoMapper donationDtoMapper;

  @Override
  public Optional<DonationDto> execute(FindDonationByIdQuery query) {
    Id id = Id.from(query.id());
    Optional<Donation> donation = donationRepository.findById(id);
    return donation.map(donationDtoMapper::fromDomain);
  }
}
