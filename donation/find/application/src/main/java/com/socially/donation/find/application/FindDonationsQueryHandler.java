package com.socially.donation.find.application;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.output.mapper.FindDonationDtoMapper;
import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.domain.port.right.FindDonationsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FindDonationsQueryHandler implements FindDonationsUseCase {

  private final FindDonationsRepository donationRepository;
  private final FindDonationDtoMapper donationDtoMapper;

  @Override
  public List<FindDonationDto> execute(FindDonationsQuery query) {
    return donationRepository.find().stream().map(donationDtoMapper::fromDomain).toList();
  }
}
