package com.socially.donation.create.application;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.mapper.CreateDonationCommandMapper;
import com.socially.donation.create.domain.port.right.CreateDonationRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private CreateDonationRepository donationRepository;

  @Mock private CreateDonationCommandMapper createDonationCommandMapper;

  @Mock private Clock clock;

  @InjectMocks private CreateDonationCommandHandler handler;

  @Test
  void execute_should_call_save_with_donation() {
    var command = new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description");
    Donation mappedDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedDonation);

    handler.execute(command);

    verify(donationRepository).create(mappedDonation);
  }
}
