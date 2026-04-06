package com.socially.donation.kernel.domain.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import org.junit.jupiter.api.Test;

class DonationTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DIFFERENT_ID = "550e8400-e29b-41d4-a716-446655440001";

  @Test
  void equals_should_return_true_when_ids_are_equal() {
    Donation donation1 =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Title 1"), Description.from("Description 1"));
    Donation donation2 =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Title 2"), Description.from("Description 2"));

    assertEquals(donation1, donation2);
  }

  @Test
  void equals_should_return_false_when_ids_are_different() {
    Donation donation1 =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Same Title"), Description.from("Same Description"));
    Donation donation2 =
        Donation.create(
            Id.from(DIFFERENT_ID), Title.from("Same Title"), Description.from("Same Description"));

    assertNotEquals(donation1, donation2);
  }

  @Test
  void withTitle_should_replace_title_and_keep_description_and_id() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Old Title"), Description.from("Old Description"));

    Donation updated = donation.withTitle(Title.from("New Title"));

    assertEquals(Title.from("New Title"), updated.title());
    assertEquals(Description.from("Old Description"), updated.description());
    assertEquals(Id.from(DONATION_ID), updated.id());
  }

  @Test
  void withDescription_should_replace_description_and_keep_title_and_id() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Old Title"), Description.from("Old Description"));

    Donation updated = donation.withDescription(Description.from("New Description"));

    assertEquals(Title.from("Old Title"), updated.title());
    assertEquals(Description.from("New Description"), updated.description());
    assertEquals(Id.from(DONATION_ID), updated.id());
  }
}
