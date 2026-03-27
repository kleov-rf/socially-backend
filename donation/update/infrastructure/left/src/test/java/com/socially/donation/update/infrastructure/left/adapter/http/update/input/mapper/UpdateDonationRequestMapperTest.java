package com.socially.donation.update.infrastructure.left.adapter.http.update.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.socially.donation.update.infrastructure.left.adapter.http.update.input.UpdateDonationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateDonationRequestMapperTest {

  @InjectMocks private UpdateDonationRequestMapper mapper;

  @Test
  void toCommand_should_map_donation_id() {
    var command = mapper.toCommand("id-123", new UpdateDonationRequest(null, null));

    assertEquals("id-123", command.id());
  }

  @Test
  void toCommand_should_map_donation_title() {
    var request = new UpdateDonationRequest("Updated Title", null);

    var command = mapper.toCommand("id-123", request);

    assertEquals("Updated Title", command.title());
  }

  @Test
  void toCommand_should_map_donation_title_when_title_not_updated() {
    var request = new UpdateDonationRequest(null, "Updated Description");

    var command = mapper.toCommand("id-123", request);

    assertNull(command.title());
  }

  @Test
  void toCommand_should_map_donation_description() {
    var request = new UpdateDonationRequest(null, "Updated Description");

    var command = mapper.toCommand("id-123", request);

    assertEquals("Updated Description", command.description());
  }

  @Test
  void toCommand_should_map_donation_description_when_description_not_updated() {
    var request = new UpdateDonationRequest("Updated Title", null);

    var command = mapper.toCommand("id-123", request);

    assertNull(command.description());
  }
}
