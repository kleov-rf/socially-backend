package com.socially.donation.update.infrastructure.left.adapter.http.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.update.application.DonationNotFoundException;
import com.socially.donation.update.application.input.UpdateDonationCommand;
import com.socially.donation.update.application.port.left.UpdateDonationUseCase;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationRequest;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.mapper.UpdateDonationRequestMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UpdateDonationControllerTest {
  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private UpdateDonationUseCase updateDonationUseCase;

  @Mock private UpdateDonationRequestMapper updateDonationRequestMapper;

  @InjectMocks private UpdateDonationController controller;

  @Test
  void patch_should_call_request_mapper_with_received_id_and_request_body() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description");
    var command = new UpdateDonationCommand(DONATION_ID, "Updated Title", "Updated Description");
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request)).thenReturn(command);

    controller.patch(DONATION_ID, request);

    verify(updateDonationRequestMapper).toCommand(DONATION_ID, request);
  }

  @Test
  void patch_should_call_execute_with_mapped_command() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description");
    var mappedCommand =
        new UpdateDonationCommand(DONATION_ID, "Updated Title", "Updated Description");
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request)).thenReturn(mappedCommand);

    controller.patch(DONATION_ID, request);

    verify(updateDonationUseCase).execute(mappedCommand);
  }

  @Test
  void patch_should_return_no_content() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description");
    var mappedCommand =
        new UpdateDonationCommand(DONATION_ID, "Updated Title", "Updated Description");
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request)).thenReturn(mappedCommand);

    var response = controller.patch(DONATION_ID, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void patch_should_propagate_donation_not_found_exception() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description");
    var mappedCommand =
        new UpdateDonationCommand(DONATION_ID, "Updated Title", "Updated Description");
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request)).thenReturn(mappedCommand);
    doThrow(new DonationNotFoundException(DONATION_ID))
        .when(updateDonationUseCase)
        .execute(mappedCommand);

    assertThatThrownBy(() -> controller.patch(DONATION_ID, request))
        .isInstanceOf(DonationNotFoundException.class);
  }
}
