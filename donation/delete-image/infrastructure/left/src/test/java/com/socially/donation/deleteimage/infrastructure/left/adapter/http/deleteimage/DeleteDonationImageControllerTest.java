package com.socially.donation.deleteimage.infrastructure.left.adapter.http.deleteimage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.deleteimage.application.input.DeleteDonationImageCommand;
import com.socially.donation.deleteimage.application.port.left.DeleteDonationImageUseCase;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.exception.DonationImageNotFoundException;
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
class DeleteDonationImageControllerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";

  @Mock private DeleteDonationImageUseCase deleteDonationImageUseCase;
  @Mock private Principal principal;
  @InjectMocks private DeleteDonationImageController controller;

  @Test
  void delete_should_pass_donation_id_image_id_and_principal_in_command() {
    controller.delete(DONATION_ID, IMAGE_ID, principal);

    ArgumentCaptor<DeleteDonationImageCommand> commandCaptor =
        ArgumentCaptor.forClass(DeleteDonationImageCommand.class);
    verify(deleteDonationImageUseCase).execute(commandCaptor.capture());
    assertThat(commandCaptor.getValue().donationId()).isEqualTo(DONATION_ID);
    assertThat(commandCaptor.getValue().imageId()).isEqualTo(IMAGE_ID);
    assertThat(commandCaptor.getValue().principal()).isEqualTo(principal);
  }

  @Test
  void delete_should_return_no_content_when_handler_succeeds() {
    var response = controller.delete(DONATION_ID, IMAGE_ID, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void delete_should_propagate_donation_not_found_exception() {
    doThrow(new DonationNotFoundException(DONATION_ID))
        .when(deleteDonationImageUseCase)
        .execute(any(DeleteDonationImageCommand.class));

    assertThatThrownBy(() -> controller.delete(DONATION_ID, IMAGE_ID, principal))
        .isInstanceOf(DonationNotFoundException.class);
  }

  @Test
  void delete_should_propagate_donation_forbidden_exception() {
    doThrow(new DonationForbiddenException(DONATION_ID))
        .when(deleteDonationImageUseCase)
        .execute(any(DeleteDonationImageCommand.class));

    assertThatThrownBy(() -> controller.delete(DONATION_ID, IMAGE_ID, principal))
        .isInstanceOf(DonationForbiddenException.class);
  }

  @Test
  void delete_should_propagate_donation_image_not_found_exception() {
    doThrow(new DonationImageNotFoundException(Id.from(DONATION_ID), Id.from(IMAGE_ID)))
        .when(deleteDonationImageUseCase)
        .execute(any(DeleteDonationImageCommand.class));

    assertThatThrownBy(() -> controller.delete(DONATION_ID, IMAGE_ID, principal))
        .isInstanceOf(DonationImageNotFoundException.class);
  }
}
