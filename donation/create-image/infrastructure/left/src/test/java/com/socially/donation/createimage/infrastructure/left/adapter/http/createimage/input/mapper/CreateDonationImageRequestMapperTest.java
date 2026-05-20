package com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input.CreateDonationImageRequest;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationImageRequestMapperTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final Principal PRINCIPAL = () -> "user@example.com";
  private static final CreateDonationImageRequest REQUEST =
      new CreateDonationImageRequest("photo.jpg", "image/jpeg", 1024L, Boolean.TRUE);

  @InjectMocks private CreateDonationImageRequestMapper mapper;

  @Test
  void toCommand_should_map_donation_id() {
    CreateDonationImageCommand command = mapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL);

    assertEquals(DONATION_ID, command.donationId());
  }

  @Test
  void toCommand_should_map_original_file_name() {
    CreateDonationImageCommand command = mapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL);

    assertEquals("photo.jpg", command.originalFileName());
  }

  @Test
  void toCommand_should_map_content_type() {
    CreateDonationImageCommand command = mapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL);

    assertEquals("image/jpeg", command.contentType());
  }

  @Test
  void toCommand_should_map_size_bytes() {
    CreateDonationImageCommand command = mapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL);

    assertEquals(1024L, command.sizeBytes());
  }

  @Test
  void toCommand_should_map_primary() {
    CreateDonationImageCommand command = mapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL);

    assertTrue(command.primary());
  }

  @Test
  void toCommand_should_map_principal() {
    CreateDonationImageCommand command = mapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL);

    assertEquals(PRINCIPAL, command.principal());
  }
}
