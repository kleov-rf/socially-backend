package com.socially.donation.find.infrastructure.left.adapter.http.find;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.socially.donation.find.application.input.FindDonationsQuery;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.port.left.FindDonationsUseCase;
import com.socially.donation.find.domain.pagination.Metadata;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.domain.pagination.PaginationCriteria;
import com.socially.donation.find.infrastructure.left.adapter.http.find.input.mapper.FindDonationsQueryMapper;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.MetadataResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.PageResponse;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper.PageResponseMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class FindDonationsControllerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @Mock private FindDonationsUseCase findDonationsUseCase;

  @Mock private FindDonationsQueryMapper queryMapper;

  @Mock private PageResponseMapper pageResponseMapper;

  @InjectMocks private FindDonationsController controller;

  @Test
  void find_should_call_query_mapper_with_received_cursor_and_size() {
    controller.find("next-cursor", 25);

    verify(queryMapper).toQuery("next-cursor", 25);
  }

  @Test
  void find_should_call_use_case_with_mapped_query() {
    FindDonationsQuery query = new FindDonationsQuery(PaginationCriteria.create("next-cursor", 25));
    when(queryMapper.toQuery("next-cursor", 25)).thenReturn(query);

    controller.find("next-cursor", 25);

    verify(findDonationsUseCase).execute(query);
  }

  @Test
  void find_should_call_response_mapper_with_use_case_output() {
    FindDonationsQuery query = new FindDonationsQuery(PaginationCriteria.create("next-cursor", 25));
    Page<FindDonationDto> page =
        Page.create(List.of(), Metadata.create("next-cursor", "previous-cursor", 25, 100L));
    when(queryMapper.toQuery("next-cursor", 25)).thenReturn(query);
    when(findDonationsUseCase.execute(query)).thenReturn(page);

    controller.find("next-cursor", 25);

    verify(pageResponseMapper).toResponse(page);
  }

  @Test
  void find_should_return_ok_with_mapped_response() {
    var donationDto =
        new FindDonationDto(
            Id.from(DONATION_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);
    FindDonationsQuery query = new FindDonationsQuery(PaginationCriteria.create("next-cursor", 25));
    Page<FindDonationDto> page =
        Page.create(
            List.of(donationDto), Metadata.create("next-cursor", "previous-cursor", 25, 100L));
    when(queryMapper.toQuery("next-cursor", 25)).thenReturn(query);
    when(findDonationsUseCase.execute(query)).thenReturn(page);

    PageResponse<FindDonationResponse> mappedPageResponse =
        new PageResponse<>(
            List.of(
                new FindDonationResponse(
                    DONATION_ID, "Test Title", "Test Description", CREATED_AT, LAST_UPDATED_AT)),
            new MetadataResponse("next-cursor", "previous-cursor", true, true, 25, 100L));
    when(pageResponseMapper.toResponse(page)).thenReturn(mappedPageResponse);

    ResponseEntity<PageResponse<FindDonationResponse>> response =
        controller.find("next-cursor", 25);

    assertThat(response.getBody()).isEqualTo(mappedPageResponse);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}
