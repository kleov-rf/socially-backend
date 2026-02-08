package com.socially.donation.application.get;

import static org.mockito.Mockito.verify;

import com.socially.donation.application.get.input.FindDonationByIdQuery;
import com.socially.donation.domain.port.DonationRepository;
import com.socially.donation.domain.valueobject.Id;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationByIdQueryHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private DonationRepository donationRepository;

  @InjectMocks private FindDonationByIdQueryHandler handler;

  @Test
  void handle_should_call_find_by_id() {
    var query = new FindDonationByIdQuery(DONATION_ID);

    handler.handle(query);

    verify(donationRepository).findById(Id.from(DONATION_ID));
  }
}
