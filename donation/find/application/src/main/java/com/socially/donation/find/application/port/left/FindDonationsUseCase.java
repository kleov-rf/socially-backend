package com.socially.donation.find.application.port.left;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.domain.pagination.Page;

public interface FindDonationsUseCase {
  Page<FindDonationDto> execute(FindDonationsQuery query);
}
