package com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.domain.pagination.Metadata;
import com.socially.donation.find.domain.pagination.Page;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
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
class PageResponseMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");
  private static final String NEXT_CURSOR = "next-cursor";
  private static final boolean HAS_NEXT = true;
  private static final int PAGE_SIZE = 20;

  @Mock private FindDonationResponseMapper donationResponseMapper;

  @InjectMocks private PageResponseMapper mapper;

  @Test
  void toResponse_should_call_donation_response_mapper_with_each_page_item() {
    FindDonationDto donationDto = givenDonationDto();
    FindDonationResponse donationResponse = givenDonationResponse();
    when(donationResponseMapper.toResponse(donationDto)).thenReturn(donationResponse);

    mapper.toResponse(givenPage(donationDto));

    verify(donationResponseMapper).toResponse(donationDto);
  }

  @Test
  void toResponse_should_return_mapped_items() {
    FindDonationDto donationDto = givenDonationDto();
    FindDonationResponse donationResponse = givenDonationResponse();
    when(donationResponseMapper.toResponse(donationDto)).thenReturn(donationResponse);

    var result = mapper.toResponse(givenPage(donationDto));

    assertEquals(List.of(donationResponse), result.items());
  }

  @Test
  void toResponse_should_map_metadata_next_cursor() {
    FindDonationDto donationDto = givenDonationDto();
    when(donationResponseMapper.toResponse(donationDto)).thenReturn(givenDonationResponse());

    var result = mapper.toResponse(givenPage(donationDto));

    assertEquals(NEXT_CURSOR, result.page().nextCursor());
  }

  @Test
  void toResponse_should_map_metadata_has_next() {
    FindDonationDto donationDto = givenDonationDto();
    when(donationResponseMapper.toResponse(donationDto)).thenReturn(givenDonationResponse());

    var result = mapper.toResponse(givenPage(donationDto));

    assertEquals(HAS_NEXT, result.page().hasNext());
  }

  @Test
  void toResponse_should_map_metadata_size() {
    FindDonationDto donationDto = givenDonationDto();
    when(donationResponseMapper.toResponse(donationDto)).thenReturn(givenDonationResponse());

    var result = mapper.toResponse(givenPage(donationDto));

    assertEquals(PAGE_SIZE, result.page().size());
  }

  private FindDonationDto givenDonationDto() {
    return new FindDonationDto(
        Id.from(DONATION_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
        CREATED_AT,
        LAST_UPDATED_AT);
  }

  private FindDonationResponse givenDonationResponse() {
    return new FindDonationResponse(
        DONATION_ID, "Test Title", "Test Description", CREATED_AT, LAST_UPDATED_AT);
  }

  private Page<FindDonationDto> givenPage(FindDonationDto donationDto) {
    return Page.create(List.of(donationDto), Metadata.create(NEXT_CURSOR, HAS_NEXT, PAGE_SIZE));
  }
}
