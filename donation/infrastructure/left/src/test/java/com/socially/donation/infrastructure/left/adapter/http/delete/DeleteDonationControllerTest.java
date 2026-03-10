package com.socially.donation.infrastructure.left.adapter.http.delete;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import com.socially.donation.application.delete.input.DeleteDonationCommand;
import com.socially.donation.application.port.left.DeleteDonationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteDonationControllerTest {

  @Mock private DeleteDonationUseCase deleteDonationUseCase;

  @InjectMocks private DeleteDonationController controller;

  @Test
  void delete_should_call_handler_with_command() {
    String donationId = "id-123";

    controller.delete(donationId);

    var expectedCommand = new DeleteDonationCommand(donationId);
    verify(deleteDonationUseCase).execute(expectedCommand);
  }

  @Test
  void delete_should_return_no_content_status() {
    String donationId = "id-123";

    var response = controller.delete(donationId);

    assertEquals(204, response.getStatusCode().value());
  }
}
