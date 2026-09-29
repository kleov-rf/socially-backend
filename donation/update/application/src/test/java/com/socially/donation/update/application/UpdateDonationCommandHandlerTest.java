package com.socially.donation.update.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonationLocation;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donation.update.application.input.UpdateDonationCommand;
import com.socially.donation.update.application.input.UpdateDonationLocationCommand;
import com.socially.donation.update.domain.port.right.UpdateDonationRepository;
import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateDonationCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Instant LAST_UPDATED_AT = Instant.parse("2024-06-15T08:00:00Z");
  private static final Instant PATCH_AT = Instant.parse("2025-01-15T10:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";

  @Mock private FindDonationByIdRepository findDonationByIdRepository;

  @Mock private AssertDonationOwnedByPrincipalUseCase assertDonationOwnedByPrincipalUseCase;

  @Mock private UpdateDonationRepository updateDonationRepository;

  @Mock private Clock clock;

  @InjectMocks private UpdateDonationCommandHandler handler;

  @Test
  void execute_should_call_repository_find_by_id_with_correct_id() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.of("Updated Description"),
            Optional.empty(),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(findDonationByIdRepository).findById(Id.from(DONATION_ID));
  }

  @Test
  void execute_should_throw_if_donation_not_found() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.of("Updated Description"),
            Optional.empty(),
            PRINCIPAL);
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID))).thenReturn(Optional.empty());

    DonationNotFoundException exception =
        assertThrows(DonationNotFoundException.class, () -> handler.execute(command));

    assertEquals("Donation not found: " + DONATION_ID, exception.getMessage());
  }

  @Test
  void execute_should_call_assert_donation_owned_by_principal_with_donation_and_principal() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.of("Updated Description"),
            Optional.empty(),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(assertDonationOwnedByPrincipalUseCase).execute(existingDonation, PRINCIPAL);
  }

  @Test
  void execute_should_not_call_update_when_ownership_assertion_fails() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.of("Updated Description"),
            Optional.empty(),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    doThrow(new DonationForbiddenException(DONATION_ID))
        .when(assertDonationOwnedByPrincipalUseCase)
        .execute(existingDonation, PRINCIPAL);

    assertThrows(DonationForbiddenException.class, () -> handler.execute(command));

    verify(updateDonationRepository, never()).update(any(Donation.class));
  }

  @Test
  void execute_should_not_call_update_if_donation_not_found() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.of("Updated Description"),
            Optional.empty(),
            PRINCIPAL);
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID))).thenReturn(Optional.empty());

    assertThrows(DonationNotFoundException.class, () -> handler.execute(command));

    verify(updateDonationRepository, never()).update(any(Donation.class));
    verifyNoInteractions(assertDonationOwnedByPrincipalUseCase);
  }

  @Test
  void execute_should_call_repository_update_with_updated_donation_with_same_id() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.of("Updated Description"),
            Optional.empty(),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(updateDonationRepository)
        .update(argThat(updatedDonation -> updatedDonation.id().equals(existingDonation.id())));
  }

  @Test
  void execute_should_call_repository_update_with_updated_donation_when_title_has_been_updated() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.empty(),
            Optional.empty(),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(updateDonationRepository)
        .update(
            argThat(
                donation ->
                    donation.title().equals(Title.from("Updated Title"))
                        && donation.lastUpdatedAt().equals(PATCH_AT)));
  }

  @Test
  void
      execute_should_call_repository_update_with_updated_donation_when_description_has_been_updated() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.empty(),
            Optional.of("Updated Description"),
            Optional.empty(),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(updateDonationRepository)
        .update(
            argThat(
                donation ->
                    donation.description().equals(Description.from("Updated Description"))
                        && donation.lastUpdatedAt().equals(PATCH_AT)));
  }

  @Test
  void
      execute_should_call_repository_update_with_updated_donation_when_title_and_description_have_been_updated() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.of("Updated Description"),
            Optional.empty(),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(updateDonationRepository)
        .update(
            argThat(
                donation ->
                    donation.title().equals(Title.from("Updated Title"))
                        && donation.description().equals(Description.from("Updated Description"))
                        && donation.lastUpdatedAt().equals(PATCH_AT)));
    verify(clock, times(1)).instant();
  }

  @Test
  void execute_should_update_location_when_location_is_provided() {
    var locationCommand = new UpdateDonationLocationCommand("Plaza Mayor 2, Madrid", 40.42, -3.71);
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.empty(),
            Optional.empty(),
            Optional.of(locationCommand),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(updateDonationRepository)
        .update(
            argThat(
                donation ->
                    donation.location().address().equals("Plaza Mayor 2, Madrid")
                        && donation.location().latitude() == 40.42
                        && donation.location().longitude() == -3.71
                        && donation.lastUpdatedAt().equals(PATCH_AT)));
  }

  @Test
  void execute_should_keep_title_and_description_when_only_location_is_updated() {
    var locationCommand = new UpdateDonationLocationCommand("Plaza Mayor 2, Madrid", 40.42, -3.71);
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.empty(),
            Optional.empty(),
            Optional.of(locationCommand),
            PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(updateDonationRepository)
        .update(
            argThat(
                donation ->
                    donation.title().equals(Title.from("Old Title"))
                        && donation.description().equals(Description.from("Old Description"))));
  }

  @Test
  void execute_should_keep_location_when_only_title_is_updated() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID,
            Optional.of("Updated Title"),
            Optional.empty(),
            Optional.empty(),
            PRINCIPAL);
    DonationLocation originalLocation =
        DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            originalLocation,
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));
    when(clock.instant()).thenReturn(PATCH_AT);

    handler.execute(command);

    verify(updateDonationRepository)
        .update(argThat(donation -> donation.location().equals(originalLocation)));
  }

  @Test
  void execute_should_not_call_clock_when_no_fields_are_provided() {
    var command =
        new UpdateDonationCommand(
            DONATION_ID, Optional.empty(), Optional.empty(), Optional.empty(), PRINCIPAL);
    Donation existingDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Old Title"),
            Description.from("Old Description"),
            DonationLocation.from("Calle Mayor 1, Madrid", 40.4168, -3.7038),
            CREATED_AT,
            LAST_UPDATED_AT);

    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(existingDonation));

    handler.execute(command);

    verify(clock, never()).instant();
    verify(updateDonationRepository)
        .update(argThat(donation -> donation.lastUpdatedAt().equals(LAST_UPDATED_AT)));
  }
}
