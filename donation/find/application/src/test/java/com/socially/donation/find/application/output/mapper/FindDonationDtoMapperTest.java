package com.socially.donation.find.application.output.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class FindDonationDtoMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  private final FindDonationDtoMapper mapper = new FindDonationDtoMapper();

  @Test
  void fromDomain_should_map_id() {
    Id expectedId = Id.from(DONATION_ID);
    var donation =
        Donation.create(
            expectedId,
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);

    FindDonationDto response = mapper.fromDomain(donation);

    assertEquals(expectedId, response.id());
  }

  @Test
  void fromDomain_should_map_title() {
    Title expectedTitle = Title.from("Test Title");
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            expectedTitle,
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    FindDonationDto response = mapper.fromDomain(donation);

    assertEquals(expectedTitle, response.title());
  }

  @Test
  void fromDomain_should_map_description() {
    Description expectedDescription = Description.from("Test Description");
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            expectedDescription,
            CREATED_AT,
            LAST_UPDATED_AT);

    FindDonationDto response = mapper.fromDomain(donation);

    assertEquals(expectedDescription, response.description());
  }

  @Test
  void fromDomain_should_map_created_at() {
    Instant expectedCreatedAt = CREATED_AT;
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            expectedCreatedAt,
            LAST_UPDATED_AT);

    FindDonationDto response = mapper.fromDomain(donation);

    assertEquals(expectedCreatedAt, response.createdAt());
  }

  @Test
  void fromDomain_should_map_last_updated_at() {
    Instant expectedLastUpdatedAt = LAST_UPDATED_AT;
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            expectedLastUpdatedAt);

    FindDonationDto response = mapper.fromDomain(donation);

    assertEquals(expectedLastUpdatedAt, response.lastUpdatedAt());
  }
}
