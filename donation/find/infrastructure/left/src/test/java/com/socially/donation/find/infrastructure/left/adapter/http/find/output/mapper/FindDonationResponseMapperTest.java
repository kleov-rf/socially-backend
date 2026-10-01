package com.socially.donation.find.infrastructure.left.adapter.http.find.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.find.application.output.FindDonationDto;
import com.socially.donation.find.application.output.FindDonorSummaryDto;
import com.socially.donation.find.infrastructure.left.adapter.http.find.output.FindDonationResponse;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindDonationResponseMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");
  private static final FindDonationDto DONATION_DTO =
      new FindDonationDto(
          Id.from(DONATION_ID),
          Title.from("Test Title"),
          Description.from("Test Description"),
          CREATED_AT,
          LAST_UPDATED_AT,
          FindDonorSummaryDto.create(DONOR_ID)
              .withGivenName(Optional.of("Jane"))
              .withFamilyName(Optional.of("Doe")));

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

  @Test
  void toResponse_should_map_donor_id() {
    FindDonationResponse response = mapper.toResponse(DONATION_DTO);

    assertEquals(DONOR_ID, response.donor().id());
  }

  @Test
  void toResponse_should_map_donor_given_name() {
    FindDonationResponse response = mapper.toResponse(DONATION_DTO);

    assertEquals(Optional.of("Jane"), response.donor().givenName());
  }

  @Test
  void toResponse_should_map_donor_family_name() {
    FindDonationResponse response = mapper.toResponse(DONATION_DTO);

    assertEquals(Optional.of("Doe"), response.donor().familyName());
  }
}
