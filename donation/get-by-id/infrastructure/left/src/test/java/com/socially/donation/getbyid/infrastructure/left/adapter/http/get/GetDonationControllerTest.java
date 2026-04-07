package com.socially.donation.getbyid.infrastructure.left.adapter.http.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.getbyid.application.input.FindDonationByIdQuery;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.port.left.FindDonationByIdUseCase;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper.DonationResponseMapper;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
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
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @Mock private FindDonationByIdUseCase findDonationByIdUseCase;

  @Mock private DonationResponseMapper mapper;

  @InjectMocks private GetDonationController controller;

  @Test
  void get_should_call_handler_with_query() {
    controller.get(DONATION_ID);

    verify(findDonationByIdUseCase).execute(new FindDonationByIdQuery(DONATION_ID));
  }

  @Test
  void get_should_call_mapper_with_result() {
    var queryResult =
        new DonationDto(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    when(findDonationByIdUseCase.execute(new FindDonationByIdQuery(DONATION_ID)))
        .thenReturn(Optional.of(queryResult));

    controller.get(DONATION_ID);

    verify(mapper).toResponse(queryResult);
  }

  @Test
  void get_should_return_ok_response_if_donation_found() {
    var queryResult =
        new DonationDto(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    var expectedResponse =
        new DonationResponseDto(
            DONATION_ID, "Test Title", "Test Description", CREATED_AT, LAST_UPDATED_AT);
    when(findDonationByIdUseCase.execute(new FindDonationByIdQuery(DONATION_ID)))
        .thenReturn(Optional.of(queryResult));
    when(mapper.toResponse(queryResult)).thenReturn(expectedResponse);

    var response = controller.get(DONATION_ID);

    assertThat(response.getBody()).isEqualTo(expectedResponse);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void get_should_return_not_found_response_if_donation_not_found() {
    when(findDonationByIdUseCase.execute(new FindDonationByIdQuery(DONATION_ID)))
        .thenReturn(Optional.empty());

    var response = controller.get(DONATION_ID);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}
