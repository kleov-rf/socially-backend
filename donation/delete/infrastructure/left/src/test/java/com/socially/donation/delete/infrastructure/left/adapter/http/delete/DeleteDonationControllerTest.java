package com.socially.donation.delete.infrastructure.left.adapter.http.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.application.port.left.DeleteDonationUseCase;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class DeleteDonationControllerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private DeleteDonationUseCase deleteDonationUseCase;
  @Mock private Principal principal;
  @InjectMocks private DeleteDonationController controller;

  @Test
  void delete_should_pass_principal_in_command() {
    controller.delete(DONATION_ID, principal);

    ArgumentCaptor<DeleteDonationCommand> commandCaptor =
        ArgumentCaptor.forClass(DeleteDonationCommand.class);
    verify(deleteDonationUseCase).execute(commandCaptor.capture());
    assertThat(commandCaptor.getValue().id()).isEqualTo(DONATION_ID);
    assertThat(commandCaptor.getValue().principal()).isEqualTo(principal);
  }

  @Test
  void delete_should_call_handler_with_command() {
    controller.delete(DONATION_ID, principal);

    verify(deleteDonationUseCase).execute(any(DeleteDonationCommand.class));
  }

  @Test
  void delete_should_return_no_content_when_handler_succeeds() {
    var response = controller.delete(DONATION_ID, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void delete_should_propagate_donation_not_found_exception() {
    doThrow(new DonationNotFoundException(DONATION_ID))
        .when(deleteDonationUseCase)
        .execute(any(DeleteDonationCommand.class));

    assertThatThrownBy(() -> controller.delete(DONATION_ID, principal))
        .isInstanceOf(DonationNotFoundException.class);
  }

  @Test
  void delete_should_propagate_donation_forbidden_exception() {
    doThrow(new DonationForbiddenException(DONATION_ID))
        .when(deleteDonationUseCase)
        .execute(any(DeleteDonationCommand.class));

    assertThatThrownBy(() -> controller.delete(DONATION_ID, principal))
        .isInstanceOf(DonationForbiddenException.class);
  }
}
