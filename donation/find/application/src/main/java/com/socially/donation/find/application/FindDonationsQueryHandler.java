package com.socially.donation.find.application;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.output.mapper.FindDonationDtoMapper;
import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.port.right.FindDonationsRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public final class FindDonationsQueryHandler implements FindDonationsUseCase {

  private final FindDonationsRepository donationRepository;
  private final FindDonationDtoMapper donationDtoMapper;

  @Override
  public Page<FindDonationDto> execute(FindDonationsQuery query) {
    Page<Donation> donations =
        donationRepository.find(query.paginationCriteria(), query.filterCriteria());
    return Page.create(
        donations.items().stream().map(donationDtoMapper::fromDomain).toList(),
        donations.metadata());
  }
}
