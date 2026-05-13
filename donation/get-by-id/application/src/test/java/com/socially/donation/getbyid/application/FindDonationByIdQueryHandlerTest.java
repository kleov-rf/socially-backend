package com.socially.donation.getbyid.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.input.FindDonationByIdQuery;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.output.mapper.DonationDtoMapper;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonorId;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationByIdQueryHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @Mock private FindDonationByIdRepository donationRepository;

  @Mock private DonationDtoMapper donationDtoMapper;

  @InjectMocks private FindDonationByIdQueryHandler handler;

  @Test
  void execute_should_call_find_by_id() {
    var query = new FindDonationByIdQuery(DONATION_ID);

    handler.execute(query);

    verify(donationRepository).findById(Id.from(DONATION_ID));
  }

  @Test
  void execute_should_return_donation_dto_if_donation_found() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    when(donationRepository.findById(any(Id.class))).thenReturn(Optional.of(donation));
    DonationDto mappedDto =
        new DonationDto(
            donation.id(),
            donation.title(),
            donation.description(),
            donation.createdAt(),
            donation.lastUpdatedAt());
    when(donationDtoMapper.fromDomain(donation)).thenReturn(mappedDto);

    var donationDto = handler.execute(new FindDonationByIdQuery(DONATION_ID)).orElseThrow();

    assertEquals(mappedDto, donationDto);
  }

  @Test
  void execute_should_return_empty_if_donation_not_found() {
    when(donationRepository.findById(any(Id.class))).thenReturn(Optional.empty());

    var result = handler.execute(new FindDonationByIdQuery(DONATION_ID));

    assertEquals(Optional.empty(), result);
  }
}
