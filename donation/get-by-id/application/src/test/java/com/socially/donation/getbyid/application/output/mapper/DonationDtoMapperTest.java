package com.socially.donation.getbyid.application.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationDtoMapperTest {

  @InjectMocks private DonationDtoMapper donationDtoMapper;

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  private static Donation sampleDonation() {
    return Donation.create(
        Id.from(DONATION_ID),
        Id.from(DONOR_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
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
  void fromDomain_should_map_donation_id_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals(Id.from(DONATION_ID), result.id());
  }

  @Test
  void fromDomain_should_map_donation_title_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals(Title.from("Test Title"), result.title());
  }

  @Test
  void fromDomain_should_map_donation_description_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals(Description.from("Test Description"), result.description());
  }

  @Test
  void fromDomain_should_map_donation_created_at_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals(CREATED_AT, result.createdAt());
  }

  @Test
  void fromDomain_should_map_donation_last_updated_at_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals(LAST_UPDATED_AT, result.lastUpdatedAt());
  }

  @Test
  void fromDomain_should_map_donor_id_to_summary() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals(DONOR_ID, result.donor().id());
  }

  @Test
  void fromDomain_should_map_donor_email_to_summary() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals("donor@example.com", result.donor().email());
  }

  @Test
  void fromDomain_should_map_donor_given_name_to_summary() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals("Jane", result.donor().givenName());
  }

  @Test
  void fromDomain_should_map_donor_family_name_to_summary() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor());

    assertEquals("Doe", result.donor().familyName());
  }
}
