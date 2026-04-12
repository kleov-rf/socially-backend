package com.socially.donation.find.infrastructure.left.adapter.http.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper.FindDonationResponseMapper;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class FindDonationsControllerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @Mock private FindDonationsUseCase findDonationsUseCase;

  @Mock private FindDonationResponseMapper mapper;

  @InjectMocks private FindDonationsController controller;

  @Test
  void find_should_call_use_case_with_empty_query() {
    controller.find();

    verify(findDonationsUseCase).execute(new FindDonationsQuery());
  }

  @Test
  void find_should_call_mapper_with_each_retrieved_donation() {
    var firstDonationDto =
        new FindDonationDto(
            Id.from(DONATION_ID),
            Title.from("First Test Title"),
            Description.from("First Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    var secondDonationDto =
        new FindDonationDto(
            Id.from(DONATION_ID),
            Title.from("Second Test Title"),
            Description.from("Second Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    when(findDonationsUseCase.execute(new FindDonationsQuery()))
        .thenReturn(List.of(firstDonationDto, secondDonationDto));

    controller.find();

    verify(mapper).toResponse(firstDonationDto);
    verify(mapper).toResponse(secondDonationDto);
  }

  @Test
  void find_should_return_ok_response_with_mapped_donation_response() {
    var donationDto =
        new FindDonationDto(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    when(findDonationsUseCase.execute(new FindDonationsQuery())).thenReturn(List.of(donationDto));

    var mappedDonationResponse =
        new FindDonationResponse(
            DONATION_ID, "Test Title", "Test Description", CREATED_AT, LAST_UPDATED_AT);
    when(mapper.toResponse(donationDto)).thenReturn(mappedDonationResponse);

    var response = controller.find();

    assertThat(response.getBody()).containsExactly(mappedDonationResponse);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
