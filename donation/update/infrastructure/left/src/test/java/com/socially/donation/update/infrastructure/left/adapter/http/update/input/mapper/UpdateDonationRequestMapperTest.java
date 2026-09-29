package com.socially.donation.update.infrastructure.left.adapter.http.update.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.donation.update.application.input.UpdateDonationLocationCommand;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationLocationRequest;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationRequest;
import java.security.Principal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateDonationRequestMapperTest {

  private static final UpdateDonationLocationRequest LOCATION_REQUEST =
      new UpdateDonationLocationRequest("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  @Mock private Principal principal;

  @InjectMocks private UpdateDonationRequestMapper mapper;

  @Test
  void toCommand_should_map_donation_id() {
    var command =
        mapper.toCommand(
            "id-123",
            new UpdateDonationRequest(Optional.empty(), Optional.empty(), Optional.empty()),
            principal);

    assertEquals("id-123", command.id());
  }

  @Test
  void toCommand_should_map_principal() {
    var command =
        mapper.toCommand(
            "id-123",
            new UpdateDonationRequest(Optional.empty(), Optional.empty(), Optional.empty()),
            principal);

    assertEquals(principal, command.principal());
  }

  @Test
  void toCommand_should_map_donation_title() {
    var request =
        new UpdateDonationRequest(Optional.of("Updated Title"), Optional.empty(), Optional.empty());

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(Optional.of("Updated Title"), command.title());
  }

  @Test
  void toCommand_should_map_donation_title_when_title_not_updated() {
    var request =
        new UpdateDonationRequest(
            Optional.empty(), Optional.of("Updated Description"), Optional.empty());

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(Optional.empty(), command.title());
  }

  @Test
  void toCommand_should_map_donation_description() {
    var request =
        new UpdateDonationRequest(
            Optional.empty(), Optional.of("Updated Description"), Optional.empty());

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(Optional.of("Updated Description"), command.description());
  }

  @Test
  void toCommand_should_map_donation_description_when_description_not_updated() {
    var request =
        new UpdateDonationRequest(Optional.of("Updated Title"), Optional.empty(), Optional.empty());

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(Optional.empty(), command.description());
  }

  @Test
  void toCommand_should_map_location_address() {
    var request =
        new UpdateDonationRequest(
            Optional.empty(), Optional.empty(), Optional.of(LOCATION_REQUEST));

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(
        Optional.of("Calle Mayor 1, Madrid"),
        command.location().map(UpdateDonationLocationCommand::address));
  }

  @Test
  void toCommand_should_map_location_latitude() {
    var request =
        new UpdateDonationRequest(
            Optional.empty(), Optional.empty(), Optional.of(LOCATION_REQUEST));

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(
        Optional.of(40.4168), command.location().map(UpdateDonationLocationCommand::latitude));
  }

  @Test
  void toCommand_should_map_location_longitude() {
    var request =
        new UpdateDonationRequest(
            Optional.empty(), Optional.empty(), Optional.of(LOCATION_REQUEST));

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(
        Optional.of(-3.7038), command.location().map(UpdateDonationLocationCommand::longitude));
  }

  @Test
  void toCommand_should_map_location_as_empty_when_location_not_updated() {
    var request =
        new UpdateDonationRequest(Optional.of("Updated Title"), Optional.empty(), Optional.empty());

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(Optional.empty(), command.location());
  }
}
