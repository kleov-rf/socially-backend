package com.socially.donation.createimage.infrastructure.left.adapter.http.createimage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.createimage.application.output.CreateDonationImageResult;
import com.socially.donation.createimage.application.port.left.CreateDonationImageUseCase;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input.CreateDonationImageRequest;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.input.mapper.CreateDonationImageRequestMapper;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output.CreateDonationImageResponse;
import com.socially.donation.createimage.infrastructure.left.adapter.http.createimage.output.mapper.CreateDonationImageResponseMapper;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CreateDonationImageControllerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String UPLOAD_URL = "https://s3.example.com/upload";
  private static final String MEDIA_URL = "https://cdn.example.com/media/key";

  private static final Principal PRINCIPAL = () -> "user@example.com";
  private static final CreateDonationImageRequest REQUEST =
      new CreateDonationImageRequest("photo.jpg", "image/jpeg", 1024L, true);

  @Mock private CreateDonationImageUseCase createDonationImageUseCase;
  @Mock private CreateDonationImageRequestMapper requestMapper;
  @Mock private CreateDonationImageResponseMapper responseMapper;
  @InjectMocks private CreateDonationImageController controller;

  private static CreateDonationImageCommand command() {
    return new CreateDonationImageCommand(
        DONATION_ID, "photo.jpg", "image/jpeg", 1024L, true, PRINCIPAL);
  }

  private static CreateDonationImageResult result() {
    return new CreateDonationImageResult(
        IMAGE_ID, UPLOAD_URL, MEDIA_URL, "image/jpeg", 1024L, true);
  }

  private static CreateDonationImageResponse response() {
    return new CreateDonationImageResponse(
        IMAGE_ID, UPLOAD_URL, MEDIA_URL, "image/jpeg", 1024L, true);
  }

  @Test
  void create_should_call_request_mapper_with_donation_id_request_and_principal() {
    controller.create(DONATION_ID, REQUEST, PRINCIPAL);

    verify(requestMapper).toCommand(DONATION_ID, REQUEST, PRINCIPAL);
  }

  @Test
  void create_should_call_use_case_with_mapped_command() {
    var mappedCommand = command();
    when(requestMapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL)).thenReturn(mappedCommand);

    controller.create(DONATION_ID, REQUEST, PRINCIPAL);

    verify(createDonationImageUseCase).execute(mappedCommand);
  }

  @Test
  void create_should_call_response_mapper_with_use_case_result() {
    var mappedCommand = command();
    var useCaseResult = result();
    when(requestMapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL)).thenReturn(mappedCommand);
    when(createDonationImageUseCase.execute(mappedCommand)).thenReturn(useCaseResult);

    controller.create(DONATION_ID, REQUEST, PRINCIPAL);

    verify(responseMapper).toResponse(useCaseResult);
  }

  @Test
  void create_should_return_created_status_with_response_body() {
    var mappedCommand = command();
    var useCaseResult = result();
    var mappedResponse = response();
    when(requestMapper.toCommand(DONATION_ID, REQUEST, PRINCIPAL)).thenReturn(mappedCommand);
    when(createDonationImageUseCase.execute(mappedCommand)).thenReturn(useCaseResult);
    when(responseMapper.toResponse(useCaseResult)).thenReturn(mappedResponse);

    ResponseEntity<CreateDonationImageResponse> responseEntity =
        controller.create(DONATION_ID, REQUEST, PRINCIPAL);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseEntity.getBody()).isEqualTo(mappedResponse);
  }
}
