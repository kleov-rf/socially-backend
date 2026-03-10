package com.socially.donation.application.create;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.application.create.input.CreateDonationCommand;
import com.socially.donation.application.create.input.mapper.CreateDonationCommandMapper;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.right.DonationRepository;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private DonationRepository donationRepository;

  @Mock private CreateDonationCommandMapper createDonationCommandMapper;

  @InjectMocks private CreateDonationCommandHandler handler;

  @Test
  void execute_should_call_save_with_donation() {
    var command = new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description");
    Donation mappedDonation =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));
    when(createDonationCommandMapper.toDomain(command)).thenReturn(mappedDonation);

    handler.execute(command);

    verify(donationRepository).save(mappedDonation);
  }
}
