package com.socially.donation.update.infrastructure.left.adapter.http.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donation.update.application.input.UpdateDonationCommand;
import com.socially.donation.update.application.port.left.UpdateDonationUseCase;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationRequest;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.mapper.UpdateDonationRequestMapper;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UpdateDonationControllerTest {
  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private UpdateDonationUseCase updateDonationUseCase;

  @Mock private UpdateDonationRequestMapper updateDonationRequestMapper;

  @Mock private Principal principal;

  @InjectMocks private UpdateDonationController controller;

  @Test
  void patch_should_pass_principal_in_command() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description", null);
    var command =
        new UpdateDonationCommand(
            DONATION_ID, "Updated Title", "Updated Description", null, principal);
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request, principal))
        .thenReturn(command);

    controller.patch(DONATION_ID, request, principal);

    ArgumentCaptor<UpdateDonationCommand> commandCaptor =
        ArgumentCaptor.forClass(UpdateDonationCommand.class);
    verify(updateDonationUseCase).execute(commandCaptor.capture());
    assertThat(commandCaptor.getValue().id()).isEqualTo(DONATION_ID);
    assertThat(commandCaptor.getValue().principal()).isEqualTo(principal);
  }

  @Test
  void patch_should_call_request_mapper_with_received_id_request_body_and_principal() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description", null);
    var command =
        new UpdateDonationCommand(
            DONATION_ID, "Updated Title", "Updated Description", null, principal);
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request, principal))
        .thenReturn(command);

    controller.patch(DONATION_ID, request, principal);

    verify(updateDonationRequestMapper).toCommand(DONATION_ID, request, principal);
  }

  @Test
  void patch_should_call_execute_with_mapped_command() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description", null);
    var mappedCommand =
        new UpdateDonationCommand(
            DONATION_ID, "Updated Title", "Updated Description", null, principal);
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request, principal))
        .thenReturn(mappedCommand);

    controller.patch(DONATION_ID, request, principal);

    verify(updateDonationUseCase).execute(mappedCommand);
  }

  @Test
  void patch_should_return_no_content() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description", null);
    var mappedCommand =
        new UpdateDonationCommand(
            DONATION_ID, "Updated Title", "Updated Description", null, principal);
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request, principal))
        .thenReturn(mappedCommand);

    var response = controller.patch(DONATION_ID, request, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void patch_should_propagate_donation_not_found_exception() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description", null);
    var mappedCommand =
        new UpdateDonationCommand(
            DONATION_ID, "Updated Title", "Updated Description", null, principal);
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request, principal))
        .thenReturn(mappedCommand);
    doThrow(new DonationNotFoundException(DONATION_ID))
        .when(updateDonationUseCase)
        .execute(mappedCommand);

    assertThatThrownBy(() -> controller.patch(DONATION_ID, request, principal))
        .isInstanceOf(DonationNotFoundException.class);
  }

  @Test
  void patch_should_propagate_donation_forbidden_exception() {
    var request = new UpdateDonationRequest("Updated Title", "Updated Description", null);
    var mappedCommand =
        new UpdateDonationCommand(
            DONATION_ID, "Updated Title", "Updated Description", null, principal);
    when(updateDonationRequestMapper.toCommand(DONATION_ID, request, principal))
        .thenReturn(mappedCommand);
    doThrow(new DonationForbiddenException(DONATION_ID))
        .when(updateDonationUseCase)
        .execute(mappedCommand);

    assertThatThrownBy(() -> controller.patch(DONATION_ID, request, principal))
        .isInstanceOf(DonationForbiddenException.class);
  }
}
