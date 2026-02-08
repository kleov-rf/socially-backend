package com.socially.donation.infrastructure.left.adapter.http.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.application.get.FindDonationByIdQueryHandler;
import com.socially.donation.application.get.input.FindDonationByIdQuery;
import com.socially.donation.application.get.output.DonationDto;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import com.socially.donation.infrastructure.left.adapter.http.get.mapper.DonationResponseMapper;
import com.socially.donation.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

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

  @Test
  void get_should_call_mapper_with_result() {
    var queryResult =
        new DonationDto(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));
    when(queryHandler.handle(new FindDonationByIdQuery(DONATION_ID)))
        .thenReturn(Optional.of(queryResult));

    controller.get(DONATION_ID);

    verify(mapper).toResponse(queryResult);
  }

  @Test
  void get_should_return_ok_response_if_donation_found() {
    var queryResult =
        new DonationDto(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));
    var expectedResponse = new DonationResponseDto(DONATION_ID, "Test Title", "Test Description");
    when(queryHandler.handle(new FindDonationByIdQuery(DONATION_ID)))
        .thenReturn(Optional.of(queryResult));
    when(mapper.toResponse(queryResult)).thenReturn(expectedResponse);

    var response = controller.get(DONATION_ID);

    assertThat(response.getBody()).isEqualTo(expectedResponse);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void get_should_return_not_found_response_if_donation_not_found() {
    when(queryHandler.handle(new FindDonationByIdQuery(DONATION_ID))).thenReturn(Optional.empty());

    var response = controller.get(DONATION_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
