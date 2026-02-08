package com.socially.donation.application.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.application.get.input.FindDonationByIdQuery;
import com.socially.donation.application.get.mapper.DonationDtoMapper;
import com.socially.donation.domain.entity.Donation;
import com.socially.donation.domain.port.DonationRepository;
import com.socially.donation.domain.valueobject.Description;
import com.socially.donation.domain.valueobject.Id;
import com.socially.donation.domain.valueobject.Title;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationByIdQueryHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";

  @Mock private DonationRepository donationRepository;

  @InjectMocks private FindDonationByIdQueryHandler handler;

  @Test
  void handle_should_call_find_by_id() {
    var query = new FindDonationByIdQuery(DONATION_ID);

    handler.handle(query);

    verify(donationRepository).findById(Id.from(DONATION_ID));
  }

  @Test
  void handle_should_return_donation_dto_if_donation_found() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID), Title.from("Test Title"), Description.from("Test Description"));
    when(donationRepository.findById(any(Id.class))).thenReturn(Optional.of(donation));

    var donationDto = handler.handle(new FindDonationByIdQuery(DONATION_ID)).orElseThrow();

    assertEquals(donationDto, DonationDtoMapper.fromDomain(donation));
  }

  @Test
  void handle_should_return_empty_if_donation_not_found() {
    when(donationRepository.findById(any(Id.class))).thenReturn(Optional.empty());

    var result = handler.handle(new FindDonationByIdQuery(DONATION_ID));

    assertEquals(Optional.empty(), result);
  }
}
