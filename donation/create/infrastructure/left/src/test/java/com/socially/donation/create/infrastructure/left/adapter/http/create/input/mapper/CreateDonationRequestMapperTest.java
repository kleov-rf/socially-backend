package com.socially.donation.create.infrastructure.left.adapter.http.create.input.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateDonationRequestMapperTest {
  private CreateDonationRequestMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CreateDonationRequestMapper();
  }

  @Test
  void toCommand_should_map_request_to_command() {
    var request = new CreateDonationRequest("id-123", "Test Title", "Test Description");

    CreateDonationCommand command = mapper.toCommand(request);

    assertEquals("id-123", command.id());
    assertEquals("Test Title", command.title());
    assertEquals("Test Description", command.description());
  }
}
