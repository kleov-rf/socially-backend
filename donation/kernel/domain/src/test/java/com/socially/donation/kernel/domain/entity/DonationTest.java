package com.socially.donation.kernel.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class DonationTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DIFFERENT_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-15T08:00:00Z");
  private static final Instant OTHER_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
  private static final Instant NEW_LAST_UPDATED_AT = Instant.parse("2025-03-10T14:30:00Z");

  @Test
  void equals_should_return_true_when_ids_are_equal() {
    Donation donation1 =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Title 1"),
            Description.from("Description 1"),
            CREATED_AT,
            LAST_UPDATED_AT);
    Donation donation2 =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Title 2"),
            Description.from("Description 2"),
            OTHER_INSTANT,
            OTHER_INSTANT);

    assertEquals(donation1, donation2);
  }

  @Test
  void equals_should_return_false_when_ids_are_different() {
    Donation donation1 =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Same Title"),
            Description.from("Same Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    Donation donation2 =
        Donation.create(
            Id.from(DIFFERENT_ID),
            Title.from("Same Title"),
            Description.from("Same Description"),
            CREATED_AT,
            LAST_UPDATED_AT);

    assertNotEquals(donation1, donation2);
  }

  @Test
  void withTitle_should_replace_title_and_keep_description_and_id() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            CREATED_AT,
            LAST_UPDATED_AT);

    Donation updated = donation.withTitle(Title.from("New Title"), NEW_LAST_UPDATED_AT);

    assertEquals(Title.from("New Title"), updated.title());
    assertEquals(Description.from("Old Description"), updated.description());
    assertEquals(Id.from(DONATION_ID), updated.id());
    assertEquals(CREATED_AT, updated.createdAt());
    assertEquals(NEW_LAST_UPDATED_AT, updated.lastUpdatedAt());
  }

  @Test
  void withDescription_should_replace_description_and_keep_title_and_id() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            CREATED_AT,
            LAST_UPDATED_AT);

    Donation updated =
        donation.withDescription(Description.from("New Description"), NEW_LAST_UPDATED_AT);

    assertEquals(Title.from("Old Title"), updated.title());
    assertEquals(Description.from("New Description"), updated.description());
    assertEquals(Id.from(DONATION_ID), updated.id());
    assertEquals(CREATED_AT, updated.createdAt());
    assertEquals(NEW_LAST_UPDATED_AT, updated.lastUpdatedAt());
  }
}
