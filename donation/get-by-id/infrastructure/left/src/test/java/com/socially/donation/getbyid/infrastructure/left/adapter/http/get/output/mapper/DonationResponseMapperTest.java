package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import org.junit.jupiter.api.Test;

class DonationResponseMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  private final DonationResponseMapper mapper = new DonationResponseMapper();

  @Test
  void toResponse_should_map_dto_to_response() {
    var donationDto =
        new DonationDto(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));

    DonationResponseDto response = mapper.toResponse(donationDto);

    assertEquals(DONATION_ID, response.id());
    assertEquals("Test Title", response.title());
    assertEquals("Test Description", response.description());
  }
}
