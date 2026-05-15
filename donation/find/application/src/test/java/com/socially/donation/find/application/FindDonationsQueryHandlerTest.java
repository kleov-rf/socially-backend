package com.socially.donation.find.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.output.FindDonorSummaryDto;
import com.socially.donation.find.application.output.mapper.FindDonationDtoMapper;
import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.Metadata;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.domain.port.right.FindDonationsRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonorId;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.findbyid.application.input.FindDonorByIdQuery;
import com.socially.donor.findbyid.application.port.left.FindDonorByIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationsQueryHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String SECOND_DONATION_ID = "550e8400-e29b-41d4-a716-446655440002";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @Mock private FindDonationsRepository donationRepository;

  @Mock private FindDonorByIdUseCase findDonorByIdUseCase;

  @Mock private FindDonationDtoMapper donationDtoMapper;

  @InjectMocks private FindDonationsQueryHandler handler;

  @Test
  void execute_should_call_repository_with_received_query() {
    FindDonationsQuery query =
        new FindDonationsQuery(
            PaginationCriteria.create(
                null, PaginationCriteria.DEFAULT_SIZE, PaginationCriteria.DEFAULT_ORDER),
            FilterCriteria.create(null));
    when(donationRepository.find(query.paginationCriteria(), query.filterCriteria()))
        .thenReturn(
            Page.create(
                List.of(),
                Metadata.create(null, null, PaginationCriteria.DEFAULT_SIZE.value(), 0L)));

    handler.execute(query);

    verify(donationRepository).find(query.paginationCriteria(), query.filterCriteria());
  }

  @Test
  void execute_should_call_find_donor_by_id_for_each_retrieved_donation() {
    Donation firstDonation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("First Test Title"),
            Description.from("First Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    Donation secondDonation =
        Donation.create(
            Id.from(SECOND_DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Second Test Title"),
            Description.from("Second Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    Donor donor =
        Donor.create(Id.from(DONOR_ID), Id.from(USER_ID), "a@b.com", "A", "B", CREATED_AT);
    FindDonationsQuery query =
        new FindDonationsQuery(
            PaginationCriteria.create(null, PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER),
            FilterCriteria.create("school"));
    when(donationRepository.find(query.paginationCriteria(), query.filterCriteria()))
        .thenReturn(
            Page.create(
                List.of(firstDonation, secondDonation),
                Metadata.create("next-cursor", null, 10, 100L)));
    when(findDonorByIdUseCase.execute(new FindDonorByIdQuery(DONOR_ID)))
        .thenReturn(Optional.of(donor));
    when(donationDtoMapper.fromDomain(eq(firstDonation), eq(donor)))
        .thenReturn(mappedDto(firstDonation, donor));
    when(donationDtoMapper.fromDomain(eq(secondDonation), eq(donor)))
        .thenReturn(mappedDto(secondDonation, donor));

    handler.execute(query);

    verify(findDonorByIdUseCase, times(2)).execute(new FindDonorByIdQuery(DONOR_ID));
  }

  @Test
  void execute_should_call_mapper_with_donation_and_donor() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    Donor donor =
        Donor.create(Id.from(DONOR_ID), Id.from(USER_ID), "a@b.com", "A", "B", CREATED_AT);
    FindDonationsQuery query =
        new FindDonationsQuery(
            PaginationCriteria.create(null, PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER),
            FilterCriteria.create("school"));
    when(donationRepository.find(query.paginationCriteria(), query.filterCriteria()))
        .thenReturn(
            Page.create(
                List.of(donation), Metadata.create("next-cursor", "previous-cursor", 10, 100L)));
    when(findDonorByIdUseCase.execute(new FindDonorByIdQuery(DONOR_ID)))
        .thenReturn(Optional.of(donor));
    when(donationDtoMapper.fromDomain(donation, donor)).thenReturn(mappedDto(donation, donor));

    handler.execute(query);

    verify(donationDtoMapper).fromDomain(donation, donor);
  }

  @Test
  void execute_should_throw_when_donation_found_but_donor_missing() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    FindDonationsQuery query =
        new FindDonationsQuery(
            PaginationCriteria.create(null, PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER),
            FilterCriteria.create("school"));
    when(donationRepository.find(query.paginationCriteria(), query.filterCriteria()))
        .thenReturn(
            Page.create(
                List.of(donation), Metadata.create("next-cursor", "previous-cursor", 10, 100L)));
    when(findDonorByIdUseCase.execute(new FindDonorByIdQuery(DONOR_ID)))
        .thenReturn(Optional.empty());

    assertThrows(IllegalStateException.class, () -> handler.execute(query));
    verifyNoInteractions(donationDtoMapper);
  }

  @Test
  void execute_should_return_page_with_donation_page_items() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    Donor donor =
        Donor.create(Id.from(DONOR_ID), Id.from(USER_ID), "a@b.com", "A", "B", CREATED_AT);
    FindDonationDto mappedDto = mappedDto(donation, donor);
    FindDonationsQuery query =
        new FindDonationsQuery(
            PaginationCriteria.create(null, PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER),
            FilterCriteria.create("school"));
    when(donationRepository.find(query.paginationCriteria(), query.filterCriteria()))
        .thenReturn(
            Page.create(
                List.of(donation), Metadata.create("next-cursor", "previous-cursor", 10, 100L)));
    when(findDonorByIdUseCase.execute(new FindDonorByIdQuery(DONOR_ID)))
        .thenReturn(Optional.of(donor));
    when(donationDtoMapper.fromDomain(donation, donor)).thenReturn(mappedDto);

    Page<FindDonationDto> result = handler.execute(query);

    assertEquals(List.of(mappedDto), result.items());
  }

  @Test
  void execute_should_return_page_with_donation_page_metadata() {
    Donation donation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    Donor donor =
        Donor.create(Id.from(DONOR_ID), Id.from(USER_ID), "a@b.com", "A", "B", CREATED_AT);
    FindDonationDto mappedDto = mappedDto(donation, donor);
    FindDonationsQuery query =
        new FindDonationsQuery(
            PaginationCriteria.create(null, PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER),
            FilterCriteria.create("school"));
    when(donationRepository.find(query.paginationCriteria(), query.filterCriteria()))
        .thenReturn(
            Page.create(
                List.of(donation), Metadata.create("next-cursor", "previous-cursor", 10, 100L)));
    when(findDonorByIdUseCase.execute(new FindDonorByIdQuery(DONOR_ID)))
        .thenReturn(Optional.of(donor));
    when(donationDtoMapper.fromDomain(donation, donor)).thenReturn(mappedDto);

    Page<FindDonationDto> result = handler.execute(query);

    assertEquals("next-cursor", result.metadata().nextCursor());
    assertEquals("previous-cursor", result.metadata().previousCursor());
    assertTrue(result.metadata().hasNext());
    assertTrue(result.metadata().hasPrevious());
    assertEquals(10, result.metadata().size());
    assertEquals(100L, result.metadata().totalCount());
  }

  private static FindDonationDto mappedDto(Donation donation, Donor donor) {
    return new FindDonationDto(
        donation.id(),
        donation.title(),
        donation.description(),
        donation.createdAt(),
        donation.lastUpdatedAt(),
        new FindDonorSummaryDto(DONOR_ID, donor.givenName(), donor.familyName()));
  }
}
