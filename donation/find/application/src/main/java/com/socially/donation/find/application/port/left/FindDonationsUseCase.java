package com.socially.donation.find.application.port.left;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import java.util.List;

public interface FindDonationsUseCase {
  List<FindDonationDto> execute(FindDonationsQuery query);
}
