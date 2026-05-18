package com.socially.donation.find.application.output.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationDtoMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @InjectMocks private FindDonationDtoMapper mapper;

  private static Donation sampleDonation() {
    return Donation.create(
        Id.from(DONATION_ID),
        Id.from(DONOR_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
        DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
        CREATED_AT,
        LAST_UPDATED_AT);
  }

  private static Donor sampleDonor() {
    return Donor.create(
        Id.from(DONOR_ID),
        Id.from(USER_ID),
        "donor@example.com",
        "Jane",
        "Doe",
        Instant.parse("2024-05-01T10:00:00Z"));
  }

  @Test
  void fromDomain_should_map_id() {
    Id expectedId = Id.from(DONATION_ID);
    var donation =
        Donation.create(
            expectedId,
            Id.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    FindDonationDto response = mapper.fromDomain(donation, sampleDonor());

    assertEquals(expectedId, response.id());
  }

  @Test
  void fromDomain_should_map_title() {
    Title expectedTitle = Title.from("Test Title");
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            expectedTitle,
            Description.from("Test Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    FindDonationDto response = mapper.fromDomain(donation, sampleDonor());

    assertEquals(expectedTitle, response.title());
  }

  @Test
  void fromDomain_should_map_description() {
    Description expectedDescription = Description.from("Test Description");
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Test Title"),
            expectedDescription,
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    FindDonationDto response = mapper.fromDomain(donation, sampleDonor());

    assertEquals(expectedDescription, response.description());
  }

  @Test
  void fromDomain_should_map_created_at() {
    Instant expectedCreatedAt = CREATED_AT;
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            expectedCreatedAt,
            LAST_UPDATED_AT);

    FindDonationDto response = mapper.fromDomain(donation, sampleDonor());

    assertEquals(expectedCreatedAt, response.createdAt());
  }

  @Test
  void fromDomain_should_map_last_updated_at() {
    Instant expectedLastUpdatedAt = LAST_UPDATED_AT;
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            expectedLastUpdatedAt);

    FindDonationDto response = mapper.fromDomain(donation, sampleDonor());

    assertEquals(expectedLastUpdatedAt, response.lastUpdatedAt());
  }

  @Test
  void fromDomain_should_map_donor_id_to_summary() {
    FindDonationDto result = mapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals(DONOR_ID, result.donor().id());
  }

  @Test
  void fromDomain_should_map_donor_given_name_to_summary() {
    FindDonationDto result = mapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals("Jane", result.donor().givenName());
  }

  @Test
  void fromDomain_should_map_donor_family_name_to_summary() {
    FindDonationDto result = mapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals("Doe", result.donor().familyName());
  }
}
