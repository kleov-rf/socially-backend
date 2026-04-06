package com.socially.donation.create.application.input.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationCommandMapperTest {

  @InjectMocks private CreateDonationCommandMapper mapper;

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Test
  void toDonation_should_map_command_to_domain() {
    var command = new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description");

    Donation actual = mapper.toDomain(command, CREATED_AT);

    Donation expected =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT);
    assertEquals(expected, actual);
  }
}
