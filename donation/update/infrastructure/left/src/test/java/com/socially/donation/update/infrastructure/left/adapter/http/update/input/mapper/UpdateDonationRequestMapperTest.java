package com.socially.donation.update.infrastructure.left.adapter.http.update.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationLocationRequest;
import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationRequest;
import java.security.Principal;
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
        mapper.toCommand("id-123", new UpdateDonationRequest(null, null, null), principal);

    assertEquals("id-123", command.id());
  }

  @Test
  void toCommand_should_map_principal() {
    var command =
        mapper.toCommand("id-123", new UpdateDonationRequest(null, null, null), principal);

    assertEquals(principal, command.principal());
  }

  @Test
  void toCommand_should_map_donation_title() {
    var request = new UpdateDonationRequest("Updated Title", null, null);

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals("Updated Title", command.title());
  }

  @Test
  void toCommand_should_map_donation_title_when_title_not_updated() {
    var request = new UpdateDonationRequest(null, "Updated Description", null);

    var command = mapper.toCommand("id-123", request, principal);

    assertNull(command.title());
  }

  @Test
  void toCommand_should_map_donation_description() {
    var request = new UpdateDonationRequest(null, "Updated Description", null);

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals("Updated Description", command.description());
  }

  @Test
  void toCommand_should_map_donation_description_when_description_not_updated() {
    var request = new UpdateDonationRequest("Updated Title", null, null);

    var command = mapper.toCommand("id-123", request, principal);

    assertNull(command.description());
  }

  @Test
  void toCommand_should_map_location_address() {
    var request = new UpdateDonationRequest(null, null, LOCATION_REQUEST);

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals("Calle Mayor 1, Madrid", command.location().address());
  }

  @Test
  void toCommand_should_map_location_latitude() {
    var request = new UpdateDonationRequest(null, null, LOCATION_REQUEST);

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(40.4168, command.location().latitude());
  }

  @Test
  void toCommand_should_map_location_longitude() {
    var request = new UpdateDonationRequest(null, null, LOCATION_REQUEST);

    var command = mapper.toCommand("id-123", request, principal);

    assertEquals(-3.7038, command.location().longitude());
  }

  @Test
  void toCommand_should_map_location_as_null_when_location_not_updated() {
    var request = new UpdateDonationRequest("Updated Title", null, null);

    var command = mapper.toCommand("id-123", request, principal);

    assertNull(command.location());
  }
}
