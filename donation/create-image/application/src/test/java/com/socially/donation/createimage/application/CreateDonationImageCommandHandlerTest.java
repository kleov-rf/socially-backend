package com.socially.donation.createimage.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.createimage.application.input.mapper.CreateDonationImageCommandMapper;
import com.socially.donation.createimage.application.output.CreateDonationImageResult;
import com.socially.donation.createimage.application.output.mapper.CreateDonationImageResultMapper;
import com.socially.donation.createimage.application.output.mapper.PresignedDonationImageUploadRequestMapper;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUpload;
import com.socially.donation.createimage.domain.model.PresignedDonationImageUploadRequest;
import com.socially.donation.createimage.domain.port.right.DonationImageUploadPort;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donation.kernel.domain.exception.InvalidDonationImageException;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.update.domain.port.right.UpdateDonationRepository;
import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationImageCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-15T08:00:00Z");
  private static final Instant NOW = Instant.parse("2025-01-15T10:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";

  private static final CreateDonationImageCommand COMMAND =
      new CreateDonationImageCommand(
          DONATION_ID, "photo.jpg", "image/jpeg", 1024L, Boolean.TRUE, PRINCIPAL);

  private static final PresignedDonationImageUpload PRESIGNED_UPLOAD =
      new PresignedDonationImageUpload(
          "https://s3.example.com/upload", "https://cdn.example.com/media/key");

  @Mock private FindDonationByIdRepository findDonationByIdRepository;
  @Mock private AssertDonationOwnedByPrincipalUseCase assertDonationOwnedByPrincipalUseCase;
  @Mock private UpdateDonationRepository updateDonationRepository;
  @Mock private DonationImageUploadPort donationImageUploadPort;
  @Mock private Clock clock;

  private final CreateDonationImageCommandMapper commandMapper =
      new CreateDonationImageCommandMapper();
  private final PresignedDonationImageUploadRequestMapper uploadRequestMapper =
      new PresignedDonationImageUploadRequestMapper();
  private final CreateDonationImageResultMapper resultMapper =
      new CreateDonationImageResultMapper();

  private CreateDonationImageCommandHandler handler() {
    return new CreateDonationImageCommandHandler(
        findDonationByIdRepository,
        assertDonationOwnedByPrincipalUseCase,
        updateDonationRepository,
        commandMapper,
        uploadRequestMapper,
        resultMapper,
        donationImageUploadPort,
        clock);
  }

  private static Donation donationWithoutImages() {
    return Donation.create(
        Id.from(DONATION_ID),
        Id.from(DONOR_ID),
        Title.from("Title"),
        Description.from("Description"),
        DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
        CREATED_AT,
        LAST_UPDATED_AT);
  }

  private static Donation donationWithMaxImages() {
    Donation donation = donationWithoutImages();
    for (Integer i = 0; i < Donation.MAX_IMAGES; i++) {
      donation =
          donation.withImageAdded(
              DonationImage.create(
                  Id.from("660e8400-e29b-41d4-a716-44665544000" + i),
                  StorageObjectKey.from("donations/" + DONATION_ID + "/images/" + i + ".jpg"),
                  ContentType.from("image/jpeg"),
                  1024L,
                  Boolean.FALSE,
                  CREATED_AT));
    }
    return donation;
  }

  @Test
  void execute_should_call_find_repository_with_donation_id() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donationWithoutImages()));
    when(clock.instant()).thenReturn(NOW);
    when(donationImageUploadPort.issueUpload(any())).thenReturn(PRESIGNED_UPLOAD);

    handler().execute(COMMAND);

    verify(findDonationByIdRepository).findById(Id.from(DONATION_ID));
  }

  @Test
  void execute_should_throw_when_donation_not_found() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID))).thenReturn(Optional.empty());

    assertThrows(DonationNotFoundException.class, () -> handler().execute(COMMAND));

    verifyNoInteractions(
        assertDonationOwnedByPrincipalUseCase, updateDonationRepository, donationImageUploadPort);
  }

  @Test
  void execute_should_call_assert_donation_owned_with_donation_and_principal() {
    Donation donation = donationWithoutImages();
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(clock.instant()).thenReturn(NOW);
    when(donationImageUploadPort.issueUpload(any())).thenReturn(PRESIGNED_UPLOAD);

    handler().execute(COMMAND);

    verify(assertDonationOwnedByPrincipalUseCase).execute(donation, PRINCIPAL);
  }

  @Test
  void execute_should_not_call_update_when_assert_throws() {
    Donation donation = donationWithoutImages();
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    doThrow(new DonationForbiddenException(DONATION_ID))
        .when(assertDonationOwnedByPrincipalUseCase)
        .execute(donation, PRINCIPAL);

    assertThrows(DonationForbiddenException.class, () -> handler().execute(COMMAND));

    verify(updateDonationRepository, never()).update(any());
    verifyNoInteractions(donationImageUploadPort);
  }

  @Test
  void execute_should_call_update_repository_with_donation_including_new_image() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donationWithoutImages()));
    when(clock.instant()).thenReturn(NOW);
    when(donationImageUploadPort.issueUpload(any())).thenReturn(PRESIGNED_UPLOAD);

    handler().execute(COMMAND);

    ArgumentCaptor<Donation> donationCaptor = ArgumentCaptor.forClass(Donation.class);
    verify(updateDonationRepository).update(donationCaptor.capture());
    assertEquals(1, donationCaptor.getValue().images().size());
    assertTrue(donationCaptor.getValue().images().getFirst().primary());
  }

  @Test
  void execute_should_call_upload_port_with_mapped_request() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donationWithoutImages()));
    when(clock.instant()).thenReturn(NOW);
    when(donationImageUploadPort.issueUpload(any())).thenReturn(PRESIGNED_UPLOAD);

    handler().execute(COMMAND);

    ArgumentCaptor<PresignedDonationImageUploadRequest> requestCaptor =
        ArgumentCaptor.forClass(PresignedDonationImageUploadRequest.class);
    verify(donationImageUploadPort).issueUpload(requestCaptor.capture());
    assertEquals("image/jpeg", requestCaptor.getValue().contentType());
    assertEquals(1024L, requestCaptor.getValue().sizeBytes());
    assertTrue(
        requestCaptor
            .getValue()
            .storageObjectKey()
            .startsWith("donations/" + DONATION_ID + "/images/"));
  }

  @Test
  void execute_should_return_result_with_upload_and_media_urls() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donationWithoutImages()));
    when(clock.instant()).thenReturn(NOW);
    when(donationImageUploadPort.issueUpload(any())).thenReturn(PRESIGNED_UPLOAD);

    CreateDonationImageResult result = handler().execute(COMMAND);

    assertEquals(PRESIGNED_UPLOAD.uploadUrl(), result.uploadUrl());
    assertEquals(PRESIGNED_UPLOAD.mediaUrl(), result.mediaUrl());
    assertEquals("image/jpeg", result.contentType());
    assertEquals(1024L, result.sizeBytes());
    assertTrue(result.primary());
    assertFalse(result.imageId().isBlank());
  }

  @Test
  void execute_should_throw_when_image_limit_exceeded() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donationWithMaxImages()));
    when(clock.instant()).thenReturn(NOW);

    assertThrows(InvalidDonationImageException.class, () -> handler().execute(COMMAND));

    verify(updateDonationRepository, never()).update(any());
    verifyNoInteractions(donationImageUploadPort);
  }
}
