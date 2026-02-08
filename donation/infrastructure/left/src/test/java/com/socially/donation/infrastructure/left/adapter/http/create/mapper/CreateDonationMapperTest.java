package com.socially.donation.infrastructure.left.adapter.http.create.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.infrastructure.left.adapter.http.create.input.CreateDonationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreateDonationMapperTest {
  private CreateDonationMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new CreateDonationMapper();
  }

  @Test
  void toCommand_should_map_request_to_command() {
    var request = new CreateDonationRequestDto("id-123", "Test Title", "Test Description");

    CreateDonationCommand command = mapper.toCommand(request);

    assertEquals("id-123", command.id());
    assertEquals("Test Title", command.title());
    assertEquals("Test Description", command.description());
  }
}
