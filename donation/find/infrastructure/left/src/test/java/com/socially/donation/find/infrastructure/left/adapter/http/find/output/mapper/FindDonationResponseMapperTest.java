package com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationResponseMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");
  private static final FindDonationDto DONATION_DTO =
      new FindDonationDto(
          Id.from(DONATION_ID),
          Title.from("Test Title"),
          Description.from("Test Description"),
          CREATED_AT,
          LAST_UPDATED_AT);

  @InjectMocks private FindDonationResponseMapper mapper;

  @Test
  void toResponse_should_map_id() {
    FindDonationResponse response = mapper.toResponse(DONATION_DTO);

    assertEquals(DONATION_ID, response.id());
  }

  @Test
  void toResponse_should_map_title() {
    FindDonationResponse response = mapper.toResponse(DONATION_DTO);

    assertEquals("Test Title", response.title());
  }

  @Test
  void toResponse_should_map_description() {
    FindDonationResponse response = mapper.toResponse(DONATION_DTO);

    assertEquals("Test Description", response.description());
  }

  @Test
  void toResponse_should_map_created_at() {
    FindDonationResponse response = mapper.toResponse(DONATION_DTO);

    assertEquals(CREATED_AT, response.createdAt());
  }

  @Test
  void toResponse_should_map_last_updated_at() {
    FindDonationResponse response = mapper.toResponse(DONATION_DTO);

    assertEquals(LAST_UPDATED_AT, response.lastUpdatedAt());
  }
}
