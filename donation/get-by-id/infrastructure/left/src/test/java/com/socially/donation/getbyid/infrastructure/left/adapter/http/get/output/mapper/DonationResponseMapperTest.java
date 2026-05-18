package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.output.DonationLocationDto;
import com.socially.donation.getbyid.application.output.DonorSummaryDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DonationResponseMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  private final DonationResponseMapper mapper = new DonationResponseMapper();

  private static final DonationLocationDto LOCATION_DTO =
      new DonationLocationDto("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  private static DonationDto sampleDto() {
    return new DonationDto(
        Id.from(DONATION_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
        LOCATION_DTO,
        CREATED_AT,
        LAST_UPDATED_AT,
        new DonorSummaryDto(DONOR_ID, "donor@example.com", "Jane", "Doe"));
  }

  @Test
  void toResponse_should_map_donation_id() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(DONATION_ID, response.id());
  }

  @Test
  void toResponse_should_map_donation_title() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Test Title", response.title());
  }

  @Test
  void toResponse_should_map_donation_description() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Test Description", response.description());
  }

  @Test
  void toResponse_should_map_location_address() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Calle Mayor 1, Madrid", response.location().address());
  }

  @Test
  void toResponse_should_map_location_latitude() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(40.4168, response.location().latitude());
  }

  @Test
  void toResponse_should_map_location_longitude() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(-3.7038, response.location().longitude());
  }

  @Test
  void toResponse_should_map_donation_created_at() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(CREATED_AT, response.createdAt());
  }

  @Test
  void toResponse_should_map_donation_last_updated_at() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(LAST_UPDATED_AT, response.lastUpdatedAt());
  }

  @Test
  void toResponse_should_map_donor_id() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(DONOR_ID, response.donor().id());
  }

  @Test
  void toResponse_should_map_donor_email() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("donor@example.com", response.donor().email());
  }

  @Test
  void toResponse_should_map_donor_given_name() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Jane", response.donor().givenName());
  }

  @Test
  void toResponse_should_map_donor_family_name() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Doe", response.donor().familyName());
  }
}
