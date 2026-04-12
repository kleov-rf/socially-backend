package com.socially.donation.find.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.output.mapper.FindDonationDtoMapper;
import com.socially.donation.find.domain.port.right.FindDonationsRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationsQueryHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @Mock private FindDonationsRepository donationRepository;

  @Mock private FindDonationDtoMapper donationDtoMapper;

  @InjectMocks private FindDonationsQueryHandler handler;

  @Test
  void execute_should_call_find() {
    handler.execute(new FindDonationsQuery());

    verify(donationRepository).find();
  }

  @Test
  void execute_should_call_mapper_with_each_retrieved_donation() {
    Donation firstDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("First Test Title"),
            Description.from("First Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    Donation secondDonation =
        Donation.create(
            Id.from("550e8400-e29b-41d4-a716-446655440001"),
            Title.from("Second Test Title"),
            Description.from("Second Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    when(donationRepository.find()).thenReturn(List.of(firstDonation, secondDonation));

    handler.execute(new FindDonationsQuery());

    verify(donationDtoMapper).fromDomain(firstDonation);
    verify(donationDtoMapper).fromDomain(secondDonation);
  }

  @Test
  void execute_should_return_mapped_donations() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    when(donationRepository.find()).thenReturn(List.of(donation));
    FindDonationDto mappedDto =
        new FindDonationDto(
            donation.id(),
            donation.title(),
            donation.description(),
            donation.createdAt(),
            donation.lastUpdatedAt());
    when(donationDtoMapper.fromDomain(donation)).thenReturn(mappedDto);

    List<FindDonationDto> result = handler.execute(new FindDonationsQuery());

    assertEquals(List.of(mappedDto), result);
  }
}
