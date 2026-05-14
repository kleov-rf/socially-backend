package com.socially.donation.getbyid.application;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.input.FindDonationByIdQuery;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.output.mapper.DonationDtoMapper;
import com.socially.donation.getbyid.application.port.left.FindDonationByIdUseCase;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donor.findbyid.application.input.FindDonorByIdQuery;
import com.socially.donor.findbyid.application.port.left.FindDonorByIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FindDonationByIdQueryHandler implements FindDonationByIdUseCase {

  private final FindDonationByIdRepository donationRepository;
  private final FindDonorByIdUseCase findDonorByIdUseCase;
  private final DonationDtoMapper donationDtoMapper;

  @Override
  public Optional<DonationDto> execute(FindDonationByIdQuery query) {
    Id id = Id.from(query.id());
    Optional<Donation> donation = donationRepository.findById(id);
    if (donation.isEmpty()) {
      return Optional.empty();
    }

    Donation found = donation.get();
    Donor donor =
        findDonorByIdUseCase
            .execute(new FindDonorByIdQuery(found.donorId().value().toString()))
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Donation "
                            + found.id().value()
                            + " references missing donor "
                            + found.donorId().value()));

    return Optional.of(donationDtoMapper.fromDomain(found, donor));
  }
}
