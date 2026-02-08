package com.socially.donation.infrastructure.left.adapter.http.create;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.application.create.CreateDonationCommandHandler;
import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.infrastructure.left.adapter.http.create.input.CreateDonationRequestDto;
import com.socially.donation.infrastructure.left.adapter.http.create.mapper.CreateDonationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CreateDonationControllerTest {

  @Mock private CreateDonationCommandHandler commandHandler;

  @Mock private CreateDonationMapper mapper;

  @InjectMocks private CreateDonationController controller;

  @Test
  void create_should_call_handler_with_command() {
    var request = new CreateDonationRequestDto("id-123", "Test Title", "Test Description");
    var command = new CreateDonationCommand("id-123", "Test Title", "Test Description");
    when(mapper.toCommand(request)).thenReturn(command);

    ResponseEntity<Void> response = controller.create(request);

    verify(commandHandler).handle(command);
  }
}
