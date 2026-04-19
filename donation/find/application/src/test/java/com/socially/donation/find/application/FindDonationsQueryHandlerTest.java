package com.socially.donation.find.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.output.mapper.FindDonationDtoMapper;
import com.socially.donation.find.domain.pagination.Metadata;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
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
  void execute_should_call_repository_with_received_query() {
    FindDonationsQuery query =
        new FindDonationsQuery(PaginationCriteria.create(null, PaginationCriteria.DEFAULT_SIZE));
    when(donationRepository.find(query.paginationCriteria()))
        .thenReturn(Page.create(List.of(), Metadata.create(null, null, false, 20)));

    handler.execute(query);

    verify(donationRepository).find(query.paginationCriteria());
  }

  @Test
  void execute_should_call_donation_dto_mapper_for_every_retrieved_item() {
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
    FindDonationsQuery query = new FindDonationsQuery(PaginationCriteria.create(null, 10));
    when(donationRepository.find(query.paginationCriteria()))
        .thenReturn(
            Page.create(
                List.of(firstDonation, secondDonation),
                Metadata.create("next-cursor", null, true, 10)));

    handler.execute(query);

    verify(donationDtoMapper).fromDomain(firstDonation);
    verify(donationDtoMapper).fromDomain(secondDonation);
  }

  @Test
  void execute_should_return_page_with_donation_page_items() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    FindDonationDto mappedDto =
        new FindDonationDto(
            donation.id(),
            donation.title(),
            donation.description(),
            donation.createdAt(),
            donation.lastUpdatedAt());
    FindDonationsQuery query = new FindDonationsQuery(PaginationCriteria.create(null, 10));
    when(donationRepository.find(query.paginationCriteria()))
        .thenReturn(
            Page.create(
                List.of(donation), Metadata.create("next-cursor", "previous-cursor", true, 10)));
    when(donationDtoMapper.fromDomain(donation)).thenReturn(mappedDto);

    Page<FindDonationDto> result = handler.execute(query);

    assertEquals(List.of(mappedDto), result.items());
  }

  @Test
  void execute_should_return_page_with_donation_page_metadata() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    FindDonationDto mappedDto =
        new FindDonationDto(
            donation.id(),
            donation.title(),
            donation.description(),
            donation.createdAt(),
            donation.lastUpdatedAt());
    FindDonationsQuery query = new FindDonationsQuery(PaginationCriteria.create(null, 10));
    when(donationRepository.find(query.paginationCriteria()))
        .thenReturn(
            Page.create(
                List.of(donation), Metadata.create("next-cursor", "previous-cursor", true, 10)));
    when(donationDtoMapper.fromDomain(donation)).thenReturn(mappedDto);

    Page<FindDonationDto> result = handler.execute(query);

    assertEquals("next-cursor", result.metadata().nextCursor());
    assertEquals("previous-cursor", result.metadata().previousCursor());
    assertTrue(result.metadata().hasNext());
    assertEquals(10, result.metadata().size());
  }
}
