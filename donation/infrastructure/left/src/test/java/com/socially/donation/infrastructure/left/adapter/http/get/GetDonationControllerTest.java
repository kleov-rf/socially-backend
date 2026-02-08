package com.socially.donation.infrastructure.left.adapter.http.get;

import static org.mockito.Mockito.verify;

import com.socially.donation.application.get.FindDonationByIdQueryHandler;
import com.socially.donation.application.get.input.FindDonationByIdQuery;
import com.socially.donation.infrastructure.left.adapter.http.get.mapper.DonationResponseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetDonationControllerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private FindDonationByIdQueryHandler queryHandler;

  @Mock private DonationResponseMapper mapper;

  @InjectMocks private GetDonationController controller;

  @Test
  void get_should_call_handler_with_query() {
    controller.get(DONATION_ID);

    verify(queryHandler).handle(new FindDonationByIdQuery(DONATION_ID));
  }
}
