package com.socially.donation.create.application.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.security.Principal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationCommandMapperTest {

  @InjectMocks private CreateDonationCommandMapper mapper;

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant NOW = Instant.parse("2024-06-01T12:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";

  @Test
  void toDomain_should_map_id() {
    var command =
        new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description", PRINCIPAL);
    Donation actual = mapper.toDomain(command, DONOR_ID, NOW);

    assertEquals(Id.from(DONATION_ID), actual.id());
  }

  @Test
  void toDomain_should_map_donor_id() {
    var command =
        new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description", PRINCIPAL);
    Donation actual = mapper.toDomain(command, DONOR_ID, NOW);

    assertEquals(Id.from(DONOR_ID), actual.donorId());
  }

  @Test
  void toDomain_should_map_title() {
    var command =
        new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description", PRINCIPAL);
    Donation actual = mapper.toDomain(command, DONOR_ID, NOW);

    assertEquals(Title.from("Test Title"), actual.title());
  }

  @Test
  void toDomain_should_map_description() {
    var command =
        new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description", PRINCIPAL);
    Donation actual = mapper.toDomain(command, DONOR_ID, NOW);

    assertEquals(Description.from("Test Description"), actual.description());
  }

  @Test
  void toDomain_should_map_created_at() {
    var command =
        new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description", PRINCIPAL);
    Donation actual = mapper.toDomain(command, DONOR_ID, NOW);

    assertEquals(NOW, actual.createdAt());
  }

  @Test
  void toDomain_should_map_last_updated_at() {
    var command =
        new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description", PRINCIPAL);
    Donation actual = mapper.toDomain(command, DONOR_ID, NOW);

    assertEquals(NOW, actual.lastUpdatedAt());
  }
}
