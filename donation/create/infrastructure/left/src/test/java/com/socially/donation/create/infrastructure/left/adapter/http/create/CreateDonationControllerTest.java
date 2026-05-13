package com.socially.donation.create.infrastructure.left.adapter.http.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.port.left.CreateDonationUseCase;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.mapper.CreateDonationRequestMapper;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CreateDonationControllerTest {

  @Mock private CreateDonationUseCase createDonationUseCase;
  @Mock private CreateDonationRequestMapper mapper;
  @Mock private Principal principal;
  @InjectMocks private CreateDonationController controller;

  @Test
  void create_should_call_mapper_with_request_and_principal() {
    var request = new CreateDonationRequest("id-123", "Test Title", "Test Description");
    var command = new CreateDonationCommand("id-123", "Test Title", "Test Description", principal);
    when(mapper.toCommand(request, principal)).thenReturn(command);

    controller.create(request, principal);

    verify(mapper).toCommand(request, principal);
  }

  @Test
  void create_should_call_use_case_with_mapped_command() {
    var request = new CreateDonationRequest("id-123", "Test Title", "Test Description");
    var command = new CreateDonationCommand("id-123", "Test Title", "Test Description", principal);
    when(mapper.toCommand(request, principal)).thenReturn(command);

    controller.create(request, principal);

    verify(createDonationUseCase).execute(command);
  }

  @Test
  void create_should_return_created_status() {
    var request = new CreateDonationRequest("id-123", "Test Title", "Test Description");
    var command = new CreateDonationCommand("id-123", "Test Title", "Test Description", principal);
    when(mapper.toCommand(request, principal)).thenReturn(command);

    ResponseEntity<Void> response = controller.create(request, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }
}
