package com.socially.donation.getbyid.application.output.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.socially.donation.getbyid.application.output.DonationDto;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonorId;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DonationDtoMapperTest {

  @InjectMocks private DonationDtoMapper donationDtoMapper;

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-20T09:00:00Z");

  @Test
  void fromDomain_should_map_donation_to_dto() {
    var donation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            LAST_UPDATED_AT);

    DonationDto result = donationDtoMapper.fromDomain(donation);

    assertEquals(Id.from(DONATION_ID), result.id());
    assertEquals(Title.from("Test Title"), result.title());
    assertEquals(Description.from("Test Description"), result.description());
    assertEquals(CREATED_AT, result.createdAt());
    assertEquals(LAST_UPDATED_AT, result.lastUpdatedAt());
  }
}
