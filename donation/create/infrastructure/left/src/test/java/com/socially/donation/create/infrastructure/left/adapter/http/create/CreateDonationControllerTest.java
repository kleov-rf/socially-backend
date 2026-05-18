package com.socially.donation.create.infrastructure.left.adapter.http.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.CreateDonationLocationCommand;
import com.socially.donation.create.application.port.left.CreateDonationUseCase;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationLocationRequest;
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

  private static final CreateDonationLocationRequest LOCATION_REQUEST =
      new CreateDonationLocationRequest("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  @Mock private CreateDonationUseCase createDonationUseCase;
  @Mock private CreateDonationRequestMapper mapper;
  @Mock private Principal principal;
  @InjectMocks private CreateDonationController controller;

  private static CreateDonationRequest request() {
    return new CreateDonationRequest("id-123", "Test Title", "Test Description", LOCATION_REQUEST);
  }

  private static CreateDonationCommand command() {
    return new CreateDonationCommand(
        "id-123",
        "Test Title",
        "Test Description",
        new CreateDonationLocationCommand("Calle Mayor 1, Madrid", 40.4168, -3.7038),
        null);
  }

  @Test
  void create_should_call_mapper_with_request_and_principal() {
    var request = request();
    var mappedCommand = command();
    when(mapper.toCommand(request, principal)).thenReturn(mappedCommand);

    controller.create(request, principal);

    verify(mapper).toCommand(request, principal);
  }

  @Test
  void create_should_call_use_case_with_mapped_command() {
    var request = request();
    var mappedCommand = command();
    when(mapper.toCommand(request, principal)).thenReturn(mappedCommand);

    controller.create(request, principal);

    verify(createDonationUseCase).execute(mappedCommand);
  }

  @Test
  void create_should_return_created_status() {
    var request = request();
    var mappedCommand = command();
    when(mapper.toCommand(request, principal)).thenReturn(mappedCommand);

    ResponseEntity<Void> response = controller.create(request, principal);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }
}
