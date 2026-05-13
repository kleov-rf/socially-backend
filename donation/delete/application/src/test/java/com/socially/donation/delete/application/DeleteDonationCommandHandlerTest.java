package com.socially.donation.delete.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.domain.port.right.DeleteDonationRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteDonationCommandHandlerTest {
  @Mock private DeleteDonationRepository donationRepository;

  @InjectMocks private DeleteDonationCommandHandler handler;

  @Test
  void execute_should_call_delete_by_id() {
    var command = new DeleteDonationCommand(UUID.randomUUID().toString());

    handler.execute(command);

    Id expectedId = Id.from(command.id());
    verify(donationRepository).deleteById(expectedId);
  }
}
