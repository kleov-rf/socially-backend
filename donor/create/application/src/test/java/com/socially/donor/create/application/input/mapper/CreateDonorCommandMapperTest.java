package com.socially.donor.create.application.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.create.application.input.CreateDonorCommand;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class CreateDonorCommandMapperTest {

  private final CreateDonorCommandMapper mapper = new CreateDonorCommandMapper();

  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant NOW = Instant.parse("2024-06-01T12:00:00Z");

  private static CreateDonorCommand command() {
    return new CreateDonorCommand(USER_ID, "donor@example.com", "Jane", "Doe");
  }

  @Test
  void toDomain_should_map_donor_id_from_received_id_argument() {
    var actual = mapper.toDomain(Id.from(DONOR_ID), command(), NOW);

    assertEquals(Id.from(DONOR_ID), actual.id());
  }

  @Test
  void toDomain_should_map_user_id_from_command() {
    var actual = mapper.toDomain(Id.from(DONOR_ID), command(), NOW);

    assertEquals(Id.from(USER_ID), actual.userId());
  }

  @Test
  void toDomain_should_map_email_from_command() {
    var actual = mapper.toDomain(Id.from(DONOR_ID), command(), NOW);

    assertEquals("donor@example.com", actual.email());
  }

  @Test
  void toDomain_should_map_given_name_from_command() {
    var actual = mapper.toDomain(Id.from(DONOR_ID), command(), NOW);

    assertEquals("Jane", actual.givenName());
  }

  @Test
  void toDomain_should_map_family_name_from_command() {
    var actual = mapper.toDomain(Id.from(DONOR_ID), command(), NOW);

    assertEquals("Doe", actual.familyName());
  }

  @Test
  void toDomain_should_map_created_at_from_instant_argument() {
    var actual = mapper.toDomain(Id.from(DONOR_ID), command(), NOW);

    assertEquals(NOW, actual.createdAt());
  }
}
