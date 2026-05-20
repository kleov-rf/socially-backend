package com.socially.donation.getbyid.application.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.output.DonationImageDto;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationDtoMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");
  private static final String MEDIA_URL = "https://cdn.example.com/donations/abc/images/key.jpg";

  private static final DonationImageDto IMAGE_DTO =
      new DonationImageDto(IMAGE_ID, MEDIA_URL, "image/jpeg", 1024L, Boolean.TRUE);

  private static final List<DonationImageDto> NO_IMAGES = List.of();

  @InjectMocks private DonationDtoMapper donationDtoMapper;

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
  void fromDomain_should_map_donation_id_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(Id.from(DONATION_ID), result.id());
  }

  @Test
  void fromDomain_should_map_donation_title_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(Title.from("Test Title"), result.title());
  }

  @Test
  void fromDomain_should_map_donation_description_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(Description.from("Test Description"), result.description());
  }

  @Test
  void fromDomain_should_map_location_address() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals("Calle Mayor 1, Madrid", result.location().address());
  }

  @Test
  void fromDomain_should_map_location_latitude() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(40.4168, result.location().latitude());
  }

  @Test
  void fromDomain_should_map_location_longitude() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(-3.7038, result.location().longitude());
  }

  @Test
  void fromDomain_should_map_donation_created_at_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(CREATED_AT, result.createdAt());
  }

  @Test
  void fromDomain_should_map_donation_last_updated_at_to_dto() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(LAST_UPDATED_AT, result.lastUpdatedAt());
  }

  @Test
  void fromDomain_should_map_donor_id_to_summary() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(DONOR_ID, result.donor().id());
  }

  @Test
  void fromDomain_should_map_donor_email_to_summary() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals("donor@example.com", result.donor().email());
  }

  @Test
  void fromDomain_should_map_donor_given_name_to_summary() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals("Jane", result.donor().givenName());
  }

  @Test
  void fromDomain_should_map_donor_family_name_to_summary() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals("Doe", result.donor().familyName());
  }

  @Test
  void fromDomain_should_return_empty_images_when_passed_empty_list() {
    DonationDto result = donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), NO_IMAGES);

    assertEquals(NO_IMAGES, result.images());
  }

  @Test
  void fromDomain_should_include_passed_image_dtos() {
    DonationDto result =
        donationDtoMapper.fromDomain(sampleDonation(), sampleDonor(), List.of(IMAGE_DTO));

    assertEquals(List.of(IMAGE_DTO), result.images());
  }
}
