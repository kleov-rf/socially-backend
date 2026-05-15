package com.socially.donation.delete.infrastructure.left.adapter.http.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.socially.donation.delete.application.DonationForbiddenException;
import com.socially.donation.delete.application.DonationNotFoundException;
import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.application.port.left.DeleteDonationUseCase;
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
  void delete_should_return_not_found_when_handler_throws_donation_not_found() {
    doThrow(new DonationNotFoundException(DONATION_ID))
        .when(deleteDonationUseCase)
        .execute(any(DeleteDonationCommand.class));

    var response = controller.delete(DONATION_ID, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void delete_should_return_forbidden_when_handler_throws_donation_forbidden() {
    doThrow(new DonationForbiddenException(DONATION_ID))
        .when(deleteDonationUseCase)
        .execute(any(DeleteDonationCommand.class));

    var response = controller.delete(DONATION_ID, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}
