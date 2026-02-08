package com.socially.donation.application.get.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.application.get.output.DonationDto;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import org.junit.jupiter.api.Test;

class DonationDtoMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Test
  void fromDomain_should_map_donation_to_dto() {
    var donation =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));

    DonationDto result = DonationDtoMapper.fromDomain(donation);

    assertEquals(Id.from(DONATION_ID), result.id());
    assertEquals(Title.from("Test Title"), result.title());
    assertEquals(Description.from("Test Description"), result.description());
  }
}
