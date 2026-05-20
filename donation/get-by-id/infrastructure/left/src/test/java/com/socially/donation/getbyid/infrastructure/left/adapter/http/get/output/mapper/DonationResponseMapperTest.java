package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.getbyid.application.output.DonationImageDto;
import com.socially.donation.getbyid.application.output.DonationLocationDto;
import com.socially.donation.getbyid.application.output.DonorSummaryDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationImageResponseDto;
import com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output.DonationResponseDto;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationResponseMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String MEDIA_URL = "https://cdn.example.com/donations/abc/images/key.jpg";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  private static final DonationLocationDto LOCATION_DTO =
      new DonationLocationDto("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  private static final List<DonationImageDto> EMPTY_IMAGES = List.of();

  private static final DonationImageDto IMAGE_DTO =
      new DonationImageDto(IMAGE_ID, MEDIA_URL, "image/jpeg", 1024L, Boolean.TRUE);

  private static final DonationImageResponseDto IMAGE_RESPONSE =
      new DonationImageResponseDto(IMAGE_ID, MEDIA_URL, "image/jpeg", 1024L, Boolean.TRUE);

  @Mock private DonationImageResponseMapper donationImageResponseMapper;

  @InjectMocks private DonationResponseMapper mapper;

  @BeforeEach
  void setUp() {
    lenient().when(donationImageResponseMapper.toResponses(EMPTY_IMAGES)).thenReturn(List.of());
  }

  private static DonationDto sampleDto() {
    return new DonationDto(
        Id.from(DONATION_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
        LOCATION_DTO,
        CREATED_AT,
        LAST_UPDATED_AT,
        new DonorSummaryDto(DONOR_ID, "donor@example.com", "Jane", "Doe"),
        EMPTY_IMAGES);
  }

  private static DonationDto sampleDtoWithImages() {
    return new DonationDto(
        Id.from(DONATION_ID),
        Title.from("Test Title"),
        Description.from("Test Description"),
        LOCATION_DTO,
        CREATED_AT,
        LAST_UPDATED_AT,
        new DonorSummaryDto(DONOR_ID, "donor@example.com", "Jane", "Doe"),
        List.of(IMAGE_DTO));
  }

  @Test
  void toResponse_should_delegate_images_to_image_response_mapper() {
    DonationDto dto = sampleDto();

    mapper.toResponse(dto);

    verify(donationImageResponseMapper).toResponses(EMPTY_IMAGES);
  }

  @Test
  void toResponse_should_map_empty_images_list() {
    when(donationImageResponseMapper.toResponses(EMPTY_IMAGES)).thenReturn(List.of());

    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(List.of(), response.images());
  }

  @Test
  void toResponse_should_map_images_from_image_response_mapper() {
    List<DonationImageDto> images = List.of(IMAGE_DTO);
    when(donationImageResponseMapper.toResponses(images)).thenReturn(List.of(IMAGE_RESPONSE));

    DonationResponseDto response = mapper.toResponse(sampleDtoWithImages());

    assertEquals(List.of(IMAGE_RESPONSE), response.images());
  }

  @Test
  void toResponse_should_map_donation_id() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(DONATION_ID, response.id());
  }

  @Test
  void toResponse_should_map_donation_title() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Test Title", response.title());
  }

  @Test
  void toResponse_should_map_donation_description() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Test Description", response.description());
  }

  @Test
  void toResponse_should_map_location_address() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Calle Mayor 1, Madrid", response.location().address());
  }

  @Test
  void toResponse_should_map_location_latitude() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(40.4168, response.location().latitude());
  }

  @Test
  void toResponse_should_map_location_longitude() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(-3.7038, response.location().longitude());
  }

  @Test
  void toResponse_should_map_donation_created_at() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(CREATED_AT, response.createdAt());
  }

  @Test
  void toResponse_should_map_donation_last_updated_at() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(LAST_UPDATED_AT, response.lastUpdatedAt());
  }

  @Test
  void toResponse_should_map_donor_id() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals(DONOR_ID, response.donor().id());
  }

  @Test
  void toResponse_should_map_donor_email() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("donor@example.com", response.donor().email());
  }

  @Test
  void toResponse_should_map_donor_given_name() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Jane", response.donor().givenName());
  }

  @Test
  void toResponse_should_map_donor_family_name() {
    DonationResponseDto response = mapper.toResponse(sampleDto());

    assertEquals("Doe", response.donor().familyName());
  }
}
