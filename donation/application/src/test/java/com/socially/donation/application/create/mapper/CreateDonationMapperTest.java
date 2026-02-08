package com.socially.donation.application.create.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import org.junit.jupiter.api.Test;

class CreateDonationMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Test
  void toDonation_should_map_command_to_donation() {
    var command = new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description");

    Donation donation = CreateDonationMapper.toDonation(command);

    Donation expected =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));
    assertEquals(expected, donation);
  }
}
