package com.socially.donation.create.infrastructure.left.adapter.http.create.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationLocationRequest;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateDonationRequestMapperTest {
  private static final CreateDonationLocationRequest LOCATION_REQUEST =
      new CreateDonationLocationRequest("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  private CreateDonationRequestMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CreateDonationRequestMapper();
  }

  private static CreateDonationRequest request() {
    return new CreateDonationRequest("id-123", "Test Title", "Test Description", LOCATION_REQUEST);
  }

  @Test
  void toCommand_should_map_request_id() {
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request(), principal);

    assertEquals("id-123", command.id());
  }

  @Test
  void toCommand_should_map_request_title() {
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request(), principal);

    assertEquals("Test Title", command.title());
  }

  @Test
  void toCommand_should_map_request_description() {
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request(), principal);

    assertEquals("Test Description", command.description());
  }

  @Test
  void toCommand_should_map_location_address() {
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request(), principal);

    assertEquals("Calle Mayor 1, Madrid", command.location().address());
  }

  @Test
  void toCommand_should_map_location_latitude() {
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request(), principal);

    assertEquals(40.4168, command.location().latitude());
  }

  @Test
  void toCommand_should_map_location_longitude() {
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request(), principal);

    assertEquals(-3.7038, command.location().longitude());
  }

  @Test
  void toCommand_should_map_principal() {
    Principal principal = () -> "user@example.com";
    CreateDonationCommand command = mapper.toCommand(request(), principal);

    assertEquals(principal, command.principal());
  }
}
