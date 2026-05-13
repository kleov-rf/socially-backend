package com.socially.donation.create.infrastructure.left.adapter.http.create.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateDonationRequestMapperTest {
  private CreateDonationRequestMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CreateDonationRequestMapper();
  }

  @Test
  void toCommand_should_map_request_id() {
    var request = new CreateDonationRequest("id-123", "Test Title", "Test Description");
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request, principal);

    assertEquals("id-123", command.id());
  }

  @Test
  void toCommand_should_map_request_title() {
    var request = new CreateDonationRequest("id-123", "Test Title", "Test Description");
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request, principal);

    assertEquals("Test Title", command.title());
  }

  @Test
  void toCommand_should_map_request_description() {
    var request = new CreateDonationRequest("id-123", "Test Title", "Test Description");
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request, principal);

    assertEquals("Test Description", command.description());
  }

  @Test
  void toCommand_should_map_principal() {
    var request = new CreateDonationRequest("id-123", "Test Title", "Test Description");
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request, principal);

    assertEquals(principal, command.principal());
  }
}
