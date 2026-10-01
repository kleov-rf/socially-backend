package com.socially.donation.create.application.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.CreateDonationLocationCommand;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.kernel.domain.entity.Donor;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationCommandMapperTest {

  @InjectMocks private CreateDonationCommandMapper mapper;

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant NOW = Instant.parse("2024-06-01T12:00:00Z");
  private static final CreateDonationLocationCommand LOCATION_COMMAND =
      new CreateDonationLocationCommand("Calle Mayor 1, Madrid", 40.4168, -3.7038);
  private static final Donor DONOR =
      Donor.create(
              Id.from("550e8400-e29b-41d4-a716-446655440001"),
              Id.from("550e8400-e29b-41d4-a716-446655440010"),
              "janedoe@email.com",
              NOW)
          .withGivenName(Optional.of("Jane"))
          .withFamilyName(Optional.of("Doe"));
  private static final Principal PRINCIPAL = () -> "user@example.com";

  private static CreateDonationCommand command() {
    return new CreateDonationCommand(
        DONATION_ID, "Test Title", "Test Description", LOCATION_COMMAND, PRINCIPAL);
  }

  @Test
  void toDomain_should_map_id() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(Id.from(DONATION_ID), actual.id());
  }

  @Test
  void toDomain_should_map_donor_id() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(Id.from(DONOR.id().value().toString()), actual.donorId());
  }

  @Test
  void toDomain_should_map_title() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(Title.from("Test Title"), actual.title());
  }

  @Test
  void toDomain_should_map_description() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(Description.from("Test Description"), actual.description());
  }

  @Test
  void toDomain_should_map_location_address() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals("Calle Mayor 1, Madrid", actual.location().address());
  }

  @Test
  void toDomain_should_map_location_latitude() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(40.4168, actual.location().latitude());
  }

  @Test
  void toDomain_should_map_location_longitude() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(-3.7038, actual.location().longitude());
  }

  @Test
  void toDomain_should_map_location_as_donation_location_value_object() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(
        DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038), actual.location());
  }

  @Test
  void toDomain_should_map_created_at() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(NOW, actual.createdAt());
  }

  @Test
  void toDomain_should_map_last_updated_at() {
    Donation actual = mapper.toDomain(command(), DONOR, NOW);

    assertEquals(NOW, actual.lastUpdatedAt());
  }
}
