package com.socially.donation.find.infrastructure.left.adapter.http.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.output.FindDonorSummaryDto;
import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.domain.filter.FilterCriteria;
import com.socially.donation.find.domain.pagination.Metadata;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PageSize;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper.FindDonationsQueryMapper;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonorResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.MetadataResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.PageResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper.PageResponseMapper;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class FindDonationsControllerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @Mock private FindDonationsUseCase findDonationsUseCase;

  @Mock private FindDonationsQueryMapper queryMapper;

  @Mock private PageResponseMapper pageResponseMapper;

  @InjectMocks private FindDonationsController controller;

  @Test
  void find_should_pass_latitude_and_longitude_to_query_mapper() {
    controller.find(
        Optional.of("next-cursor"),
        Optional.of(10),
        Optional.of("nearest_first"),
        Optional.of("school"),
        Optional.of(40.4168),
        Optional.of(-3.7038));

    verify(queryMapper)
        .toQuery(
            Optional.of("next-cursor"),
            Optional.of(10),
            Optional.of("nearest_first"),
            Optional.of("school"),
            Optional.of(40.4168),
            Optional.of(-3.7038));
  }

  @Test
  void find_should_call_query_mapper_with_received_arguments() {
    controller.find(
        Optional.of("next-cursor"),
        Optional.of(10),
        Optional.of("oldest_first"),
        Optional.of("school"),
        Optional.empty(),
        Optional.empty());

    verify(queryMapper)
        .toQuery(
            Optional.of("next-cursor"),
            Optional.of(10),
            Optional.of("oldest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty());
  }

  @Test
  void find_should_call_use_case_with_mapped_query() {
    FindDonationsQuery query =
        FindDonationsQuery.create(
            PaginationCriteria.create(PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER)
                .withCursor(Optional.of("next-cursor")),
            FilterCriteria.create().withQuery(Optional.of("school")));
    when(queryMapper.toQuery(
            Optional.of("next-cursor"),
            Optional.of(10),
            Optional.of("newest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty()))
        .thenReturn(query);

    controller.find(
        Optional.of("next-cursor"),
        Optional.of(10),
        Optional.of("newest_first"),
        Optional.of("school"),
        Optional.empty(),
        Optional.empty());

    verify(findDonationsUseCase).execute(query);
  }

  @Test
  void find_should_call_response_mapper_with_use_case_output() {
    FindDonationsQuery query =
        FindDonationsQuery.create(
            PaginationCriteria.create(PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER)
                .withCursor(Optional.of("next-cursor")),
            FilterCriteria.create().withQuery(Optional.of("school")));
    Page<FindDonationDto> page =
        Page.create(
            List.of(),
            Metadata.create(10, 100L)
                .withNextCursor(Optional.of("next-cursor"))
                .withPreviousCursor(Optional.of("previous-cursor")));
    when(queryMapper.toQuery(
            Optional.of("next-cursor"),
            Optional.of(10),
            Optional.of("newest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty()))
        .thenReturn(query);
    when(findDonationsUseCase.execute(query)).thenReturn(page);

    controller.find(
        Optional.of("next-cursor"),
        Optional.of(10),
        Optional.of("newest_first"),
        Optional.of("school"),
        Optional.empty(),
        Optional.empty());

    verify(pageResponseMapper).toResponse(page);
  }

  @Test
  void find_should_return_ok_status() {
    FindDonationsQuery query =
        FindDonationsQuery.create(
            PaginationCriteria.create(PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER)
                .withCursor(Optional.of("next-cursor")),
            FilterCriteria.create().withQuery(Optional.of("school")));
    Page<FindDonationDto> page =
        Page.create(
            List.of(),
            Metadata.create(10, 100L)
                .withNextCursor(Optional.of("next-cursor"))
                .withPreviousCursor(Optional.of("previous-cursor")));
    when(queryMapper.toQuery(
            Optional.of("next-cursor"),
            Optional.of(10),
            Optional.of("newest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty()))
        .thenReturn(query);
    when(findDonationsUseCase.execute(query)).thenReturn(page);
    when(pageResponseMapper.toResponse(page)).thenReturn(emptyMappedPageResponse());

    ResponseEntity<PageResponse<FindDonationResponse>> response =
        controller.find(
            Optional.of("next-cursor"),
            Optional.of(10),
            Optional.of("newest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void find_should_return_mapped_response_body() {
    var donationDto =
        new FindDonationDto(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT,
            new FindDonorSummaryDto(DONOR_ID, "Jane", "Doe"));
    FindDonationsQuery query =
        FindDonationsQuery.create(
            PaginationCriteria.create(PageSize.TEN_ITEMS, PaginationCriteria.DEFAULT_ORDER)
                .withCursor(Optional.of("next-cursor")),
            FilterCriteria.create().withQuery(Optional.of("school")));
    Page<FindDonationDto> page =
        Page.create(
            List.of(donationDto),
            Metadata.create(10, 100L)
                .withNextCursor(Optional.of("next-cursor"))
                .withPreviousCursor(Optional.of("previous-cursor")));
    when(queryMapper.toQuery(
            Optional.of("next-cursor"),
            Optional.of(10),
            Optional.of("newest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty()))
        .thenReturn(query);
    when(findDonationsUseCase.execute(query)).thenReturn(page);

    PageResponse<FindDonationResponse> mappedPageResponse =
        new PageResponse<>(
            List.of(
                new FindDonationResponse(
                    DONATION_ID,
                    "Test Title",
                    "Test Description",
                    CREATED_AT,
                    LAST_UPDATED_AT,
                    new FindDonorResponse(DONOR_ID, "Jane", "Doe"))),
            MetadataResponse.create(true, true, 10, 100L)
                .withNextCursor(Optional.of("next-cursor"))
                .withPreviousCursor(Optional.of("previous-cursor")));
    when(pageResponseMapper.toResponse(page)).thenReturn(mappedPageResponse);

    ResponseEntity<PageResponse<FindDonationResponse>> response =
        controller.find(
            Optional.of("next-cursor"),
            Optional.of(10),
            Optional.of("newest_first"),
            Optional.of("school"),
            Optional.empty(),
            Optional.empty());

    assertThat(response.getBody()).isEqualTo(mappedPageResponse);
  }

  private static PageResponse<FindDonationResponse> emptyMappedPageResponse() {
    return new PageResponse<>(
        List.of(),
        MetadataResponse.create(true, true, 10, 100L)
            .withNextCursor(Optional.of("next-cursor"))
            .withPreviousCursor(Optional.of("previous-cursor")));
  }
}
