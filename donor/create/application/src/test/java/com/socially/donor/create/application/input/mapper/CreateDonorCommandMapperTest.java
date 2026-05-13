package com.socially.donor.create.application.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.commons.kernel.domain.valueobject.Id;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonorCommandMapperTest {

  @InjectMocks private CreateDonorCommandMapper mapper;

  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant NOW = Instant.parse("2024-06-01T12:00:00Z");

  @Test
  void toDomain_should_map_command_to_domain() {
    var command = new CreateDonorCommand(DONOR_ID, USER_ID, "donor@example.com", "Jane", "Doe");

    Donor actual = mapper.toDomain(command, NOW);

    Donor expected =
        Donor.create(
            Id.from(DONOR_ID), Id.from(USER_ID), "donor@example.com", "Jane", "Doe", NOW);
    assertEquals(expected, actual);
  }
}
