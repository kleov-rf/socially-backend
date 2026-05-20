package com.socially.donation.deleteimage.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.deleteimage.application.input.DeleteDonationImageCommand;
import com.socially.donation.deleteimage.domain.port.right.DonationImageStorageDeletePort;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.entity.DonationImage;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.exception.DonationImageNotFoundException;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donation.kernel.domain.valueobject.ContentType;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.StorageObjectKey;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.update.domain.port.right.UpdateDonationRepository;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteDonationImageCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String IMAGE_ID = "660e8400-e29b-41d4-a716-446655440001";
  private static final String SECOND_IMAGE_ID = "660e8400-e29b-41d4-a716-446655440002";
  private static final String UNKNOWN_IMAGE_ID = "660e8400-e29b-41d4-a716-446655440099";
  private static final StorageObjectKey PRIMARY_IMAGE_STORAGE_KEY =
      StorageObjectKey.from("donations/" + DONATION_ID + "/images/" + IMAGE_ID + ".jpg");
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-15T08:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";
  private static final RuntimeException STORAGE_DELETE_FAILURE =
      new RuntimeException("storage delete failed");

  private static final DeleteDonationImageCommand COMMAND =
      new DeleteDonationImageCommand(DONATION_ID, IMAGE_ID, PRINCIPAL);

  @Mock private FindDonationByIdRepository findDonationByIdRepository;
  @Mock private AssertDonationOwnedByPrincipalUseCase assertDonationOwnedByPrincipalUseCase;
  @Mock private DonationImageStorageDeletePort donationImageStorageDeletePort;
  @Mock private UpdateDonationRepository updateDonationRepository;
  @InjectMocks private DeleteDonationImageCommandHandler handler;

  private static DeleteDonationImageCommand commandWithImageId(String imageId) {
    return new DeleteDonationImageCommand(DONATION_ID, imageId, PRINCIPAL);
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

  private static DonationImage createImage(String imageId, Boolean primary) {
    return DonationImage.create(
        Id.from(imageId),
        StorageObjectKey.from("donations/" + DONATION_ID + "/images/" + imageId + ".jpg"),
        ContentType.from("image/jpeg"),
        1024L,
        primary,
        CREATED_AT);
  }

  private static Donation donationWithPrimaryAndSecondaryImages() {
    return donationWithoutImages()
        .withImageAdded(createImage(IMAGE_ID, Boolean.TRUE))
        .withImageAdded(createImage(SECOND_IMAGE_ID, Boolean.FALSE));
  }

  private void givenDonationWithImagesFound() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donationWithPrimaryAndSecondaryImages()));
  }

  @Test
  void execute_should_call_find_repository_with_donation_id() {
    givenDonationWithImagesFound();

    handler.execute(COMMAND);

    verify(findDonationByIdRepository).findById(Id.from(DONATION_ID));
  }

  @Test
  void execute_should_throw_when_donation_not_found() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID))).thenReturn(Optional.empty());

    assertThrows(DonationNotFoundException.class, () -> handler.execute(COMMAND));

    verifyNoInteractions(
        assertDonationOwnedByPrincipalUseCase,
        donationImageStorageDeletePort,
        updateDonationRepository);
  }

  @Test
  void execute_should_call_assert_donation_owned_with_donation_and_principal() {
    Donation donation = donationWithPrimaryAndSecondaryImages();
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));

    handler.execute(COMMAND);

    verify(assertDonationOwnedByPrincipalUseCase).execute(donation, PRINCIPAL);
  }

  @Test
  void execute_should_not_call_update_when_assert_throws() {
    Donation donation = donationWithPrimaryAndSecondaryImages();
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    doThrow(new DonationForbiddenException(DONATION_ID))
        .when(assertDonationOwnedByPrincipalUseCase)
        .execute(donation, PRINCIPAL);

    assertThrows(DonationForbiddenException.class, () -> handler.execute(COMMAND));

    verifyNoInteractions(donationImageStorageDeletePort);
    verify(updateDonationRepository, never()).update(any());
  }

  @Test
  void execute_should_call_storage_delete_port_with_storage_object_key() {
    givenDonationWithImagesFound();

    handler.execute(COMMAND);

    ArgumentCaptor<StorageObjectKey> keyCaptor = ArgumentCaptor.forClass(StorageObjectKey.class);
    verify(donationImageStorageDeletePort).deleteObject(keyCaptor.capture());
    assertEquals(PRIMARY_IMAGE_STORAGE_KEY, keyCaptor.getValue());
  }

  @Test
  void execute_should_call_storage_delete_before_update() {
    givenDonationWithImagesFound();

    handler.execute(COMMAND);

    InOrder inOrder = inOrder(donationImageStorageDeletePort, updateDonationRepository);
    inOrder.verify(donationImageStorageDeletePort).deleteObject(any(StorageObjectKey.class));
    inOrder.verify(updateDonationRepository).update(any(Donation.class));
  }

  @Test
  void execute_should_call_update_repository_with_donation_without_removed_image() {
    givenDonationWithImagesFound();

    handler.execute(COMMAND);

    ArgumentCaptor<Donation> donationCaptor = ArgumentCaptor.forClass(Donation.class);
    verify(updateDonationRepository).update(donationCaptor.capture());
    assertEquals(1, donationCaptor.getValue().images().size());
    assertEquals(Id.from(SECOND_IMAGE_ID), donationCaptor.getValue().images().getFirst().id());
    assertTrue(donationCaptor.getValue().images().getFirst().primary());
  }

  @Test
  void execute_should_throw_donation_image_not_found_when_image_not_on_donation() {
    givenDonationWithImagesFound();

    assertThrows(
        DonationImageNotFoundException.class,
        () -> handler.execute(commandWithImageId(UNKNOWN_IMAGE_ID)));

    verifyNoInteractions(donationImageStorageDeletePort);
    verify(updateDonationRepository, never()).update(any());
  }

  @Test
  void execute_should_call_storage_delete_port_when_storage_delete_throws() {
    givenDonationWithImagesFound();
    doThrow(STORAGE_DELETE_FAILURE)
        .when(donationImageStorageDeletePort)
        .deleteObject(any(StorageObjectKey.class));

    assertThrows(RuntimeException.class, () -> handler.execute(COMMAND));

    verify(donationImageStorageDeletePort).deleteObject(PRIMARY_IMAGE_STORAGE_KEY);
  }

  @Test
  void execute_should_not_call_update_when_storage_delete_throws() {
    givenDonationWithImagesFound();
    doThrow(STORAGE_DELETE_FAILURE)
        .when(donationImageStorageDeletePort)
        .deleteObject(any(StorageObjectKey.class));

    assertThrows(RuntimeException.class, () -> handler.execute(COMMAND));

    verify(updateDonationRepository, never()).update(any());
  }
}
