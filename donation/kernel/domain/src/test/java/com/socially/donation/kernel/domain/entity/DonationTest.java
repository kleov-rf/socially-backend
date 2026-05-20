package com.socially.donation.kernel.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.exception.InvalidDonationImageException;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DonationTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DIFFERENT_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-15T08:00:00Z");
  private static final Instant OTHER_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
  private static final Instant NEW_LAST_UPDATED_AT = Instant.parse("2025-03-10T14:30:00Z");
  private static final Title TITLE = Title.from("Title");
  private static final Description DESCRIPTION = Description.from("Description");
  private static final DonationLocation DEFAULT_LOCATION =
      DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  private static Donation createDonation() {
    return Donation.create(
        Id.from(DONATION_ID),
        Id.from(DONOR_ID),
        TITLE,
        DESCRIPTION,
        DEFAULT_LOCATION,
        CREATED_AT,
        LAST_UPDATED_AT);
  }

  @Test
  void create_should_set_id() {
    Donation donation = createDonation();

    assertEquals(Id.from(DONATION_ID), donation.id());
  }

  @Test
  void create_should_set_donor_id() {
    Donation donation = createDonation();

    assertEquals(Id.from(DONOR_ID), donation.donorId());
  }

  @Test
  void create_should_set_title() {
    Donation donation = createDonation();

    assertEquals(TITLE, donation.title());
  }

  @Test
  void create_should_set_description() {
    Donation donation = createDonation();

    assertEquals(DESCRIPTION, donation.description());
  }

  @Test
  void create_should_set_location() {
    Donation donation = createDonation();

    assertEquals(DEFAULT_LOCATION, donation.location());
  }

  @Test
  void create_should_set_created_at() {
    Donation donation = createDonation();

    assertEquals(CREATED_AT, donation.createdAt());
  }

  @Test
  void create_should_set_last_updated_at() {
    Donation donation = createDonation();

    assertEquals(LAST_UPDATED_AT, donation.lastUpdatedAt());
  }

  @Test
  void equals_should_return_true_when_ids_are_equal() {
    Donation donation1 =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Title 1"),
            Description.from("Description 1"),
            DEFAULT_LOCATION,
            CREATED_AT,
            LAST_UPDATED_AT);
    Donation donation2 =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Title 2"),
            Description.from("Description 2"),
            DEFAULT_LOCATION,
            OTHER_INSTANT,
            OTHER_INSTANT);

    assertEquals(donation1, donation2);
  }

  @Test
  void equals_should_return_false_when_ids_are_different() {
    Donation donation1 =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Same Title"),
            Description.from("Same Description"),
            DEFAULT_LOCATION,
            CREATED_AT,
            LAST_UPDATED_AT);
    Donation donation2 =
        Donation.create(
            Id.from(DIFFERENT_ID),
            Id.from(DONOR_ID),
            Title.from("Same Title"),
            Description.from("Same Description"),
            DEFAULT_LOCATION,
            CREATED_AT,
            LAST_UPDATED_AT);

    assertNotEquals(donation1, donation2);
  }

  @Test
  void belongsToDonor_should_return_true_when_donor_id_matches() {
    Donation donation = createDonation();

    assertTrue(donation.belongsToDonor(Id.from(DONOR_ID)));
  }

  @Test
  void belongsToDonor_should_return_false_when_donor_id_differs() {
    Donation donation = createDonation();

    assertFalse(donation.belongsToDonor(Id.from(DIFFERENT_ID)));
  }

  @Test
  void withTitle_should_replace_title_and_keep_description_and_id() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DEFAULT_LOCATION,
            CREATED_AT,
            LAST_UPDATED_AT);

    Donation updated = donation.withTitle(Title.from("New Title"), NEW_LAST_UPDATED_AT);

    assertEquals(Title.from("New Title"), updated.title());
    assertEquals(Description.from("Old Description"), updated.description());
    assertEquals(Id.from(DONOR_ID), updated.donorId());
    assertEquals(Id.from(DONATION_ID), updated.id());
    assertEquals(CREATED_AT, updated.createdAt());
    assertEquals(NEW_LAST_UPDATED_AT, updated.lastUpdatedAt());
    assertEquals(DEFAULT_LOCATION, updated.location());
  }

  @Test
  void create_should_throw_exception_when_id_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                Donation.create(
                    null,
                    Id.from(DONOR_ID),
                    TITLE,
                    DESCRIPTION,
                    DEFAULT_LOCATION,
                    CREATED_AT,
                    LAST_UPDATED_AT));

    assertEquals("donation id cannot be null", exception.getMessage());
  }

  @Test
  void create_should_throw_exception_when_donor_id_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                Donation.create(
                    Id.from(DONATION_ID),
                    null,
                    TITLE,
                    DESCRIPTION,
                    DEFAULT_LOCATION,
                    CREATED_AT,
                    LAST_UPDATED_AT));

    assertEquals("donation donor id cannot be null", exception.getMessage());
  }

  @Test
  void create_should_throw_exception_when_title_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                Donation.create(
                    Id.from(DONATION_ID),
                    Id.from(DONOR_ID),
                    null,
                    DESCRIPTION,
                    DEFAULT_LOCATION,
                    CREATED_AT,
                    LAST_UPDATED_AT));

    assertEquals("donation title cannot be null", exception.getMessage());
  }

  @Test
  void create_should_throw_exception_when_description_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                Donation.create(
                    Id.from(DONATION_ID),
                    Id.from(DONOR_ID),
                    TITLE,
                    null,
                    DEFAULT_LOCATION,
                    CREATED_AT,
                    LAST_UPDATED_AT));

    assertEquals("donation description cannot be null", exception.getMessage());
  }

  @Test
  void create_should_throw_exception_when_location_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                Donation.create(
                    Id.from(DONATION_ID),
                    Id.from(DONOR_ID),
                    TITLE,
                    DESCRIPTION,
                    null,
                    CREATED_AT,
                    LAST_UPDATED_AT));

    assertEquals("donation location cannot be null", exception.getMessage());
  }

  @Test
  void create_should_throw_exception_when_created_at_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                Donation.create(
                    Id.from(DONATION_ID),
                    Id.from(DONOR_ID),
                    TITLE,
                    DESCRIPTION,
                    DEFAULT_LOCATION,
                    null,
                    LAST_UPDATED_AT));

    assertEquals("donation created at cannot be null", exception.getMessage());
  }

  @Test
  void create_should_throw_exception_when_last_updated_at_is_null() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                Donation.create(
                    Id.from(DONATION_ID),
                    Id.from(DONOR_ID),
                    TITLE,
                    DESCRIPTION,
                    DEFAULT_LOCATION,
                    CREATED_AT,
                    null));

    assertEquals("donation last updated at cannot be null", exception.getMessage());
  }

  @Test
  void withTitle_should_preserve_location() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Description"),
            DEFAULT_LOCATION,
            CREATED_AT,
            LAST_UPDATED_AT);

    Donation updated = donation.withTitle(Title.from("New Title"), NEW_LAST_UPDATED_AT);

    assertEquals(DEFAULT_LOCATION, updated.location());
  }

  @Test
  void withDescription_should_replace_description_and_keep_title_and_id() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DEFAULT_LOCATION,
            CREATED_AT,
            LAST_UPDATED_AT);

    Donation updated =
        donation.withDescription(Description.from("New Description"), NEW_LAST_UPDATED_AT);

    assertEquals(Title.from("Old Title"), updated.title());
    assertEquals(Description.from("New Description"), updated.description());
    assertEquals(Id.from(DONOR_ID), updated.donorId());
    assertEquals(Id.from(DONATION_ID), updated.id());
    assertEquals(CREATED_AT, updated.createdAt());
    assertEquals(NEW_LAST_UPDATED_AT, updated.lastUpdatedAt());
    assertEquals(DEFAULT_LOCATION, updated.location());
  }

  @Test
  void withLocation_should_replace_location_and_keep_title_and_description() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Title"),
            Description.from("Description"),
            DEFAULT_LOCATION,
            CREATED_AT,
            LAST_UPDATED_AT);
    DonationLocation newLocation = DonationLocation.from("Plaza Mayor 2, Madrid", 40.42, -3.71);

    Donation updated = donation.withLocation(newLocation, NEW_LAST_UPDATED_AT);

    assertEquals(newLocation, updated.location());
    assertEquals(Title.from("Title"), updated.title());
    assertEquals(Description.from("Description"), updated.description());
    assertEquals(NEW_LAST_UPDATED_AT, updated.lastUpdatedAt());
  }

  @Test
  void withLocation_should_set_location_address() {
    Donation donation = createDonation();
    DonationLocation newLocation = DonationLocation.from("New Address", 41.0, -4.0);

    Donation updated = donation.withLocation(newLocation, NEW_LAST_UPDATED_AT);

    assertEquals("New Address", updated.location().address());
  }

  @Test
  void withDescription_should_preserve_location() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Title"),
            Description.from("Old Description"),
            DEFAULT_LOCATION,
            CREATED_AT,
            LAST_UPDATED_AT);

    Donation updated =
        donation.withDescription(Description.from("New Description"), NEW_LAST_UPDATED_AT);

    assertEquals(DEFAULT_LOCATION, updated.location());
  }

  private static DonationImage createImage(String imageId, Boolean primary) {
    return DonationImage.create(
        Id.from(imageId),
        StorageObjectKey.from("donations/" + DONATION_ID + "/images/" + imageId + ".jpg"),
        ContentType.from("image/jpeg"),
        1024L,
        primary,
        CREATED_AT);
  }

  @Test
  void withImageAdded_should_add_image_to_empty_list() {
    DonationImage image = createImage("660e8400-e29b-41d4-a716-446655440001", Boolean.TRUE);

    Donation updated = createDonation().withImageAdded(image);

    assertEquals(1, updated.images().size());
    assertEquals(image, updated.images().getFirst());
  }

  @Test
  void withImageAdded_should_clear_existing_primary_when_new_image_is_primary() {
    DonationImage first = createImage("660e8400-e29b-41d4-a716-446655440001", Boolean.TRUE);
    DonationImage second = createImage("660e8400-e29b-41d4-a716-446655440002", Boolean.TRUE);
    Donation withFirst = createDonation().withImageAdded(first);

    Donation updated = withFirst.withImageAdded(second);

    assertEquals(2, updated.images().size());
    assertFalse(updated.images().get(0).primary());
    assertTrue(updated.images().get(1).primary());
  }

  @Test
  void withImageAdded_should_throw_when_image_limit_exceeded() {
    Donation donationAtLimit = donationWithMaxImages();
    DonationImage extra = createImage("660e8400-e29b-41d4-a716-446655440099", Boolean.FALSE);

    assertThrows(InvalidDonationImageException.class, () -> donationAtLimit.withImageAdded(extra));
  }

  private static Donation donationWithMaxImages() {
    Donation donation = createDonation();
    for (Integer i = 0; i < Donation.MAX_IMAGES; i++) {
      donation =
          donation.withImageAdded(
              createImage("660e8400-e29b-41d4-a716-44665544000" + i, Boolean.FALSE));
    }
    return donation;
  }

  @Test
  void withImageAdded_should_throw_when_image_is_null() {
    assertThrows(IllegalArgumentException.class, () -> createDonation().withImageAdded(null));
  }

  @Test
  void images_should_default_to_empty_list() {
    Donation donation = createDonation();

    assertTrue(donation.images().isEmpty());
  }
}
