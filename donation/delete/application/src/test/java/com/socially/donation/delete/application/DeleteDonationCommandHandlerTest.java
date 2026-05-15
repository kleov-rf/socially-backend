package com.socially.donation.delete.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.domain.port.right.DeleteDonationRepository;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonorId;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteDonationCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String OTHER_DONOR_ID = "550e8400-e29b-41d4-a716-446655440002";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";

  @Mock private FindDonationByIdRepository findDonationByIdRepository;
  @Mock private GetCurrentAuthUserUseCase getCurrentAuthUserUseCase;
  @Mock private FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  @Mock private DeleteDonationRepository donationRepository;
  @InjectMocks private DeleteDonationCommandHandler handler;

  private DeleteDonationCommand command;
  private User user;
  private Donation donation;
  private Donor donor;

  @BeforeEach
  void setUp() {
    command = new DeleteDonationCommand(DONATION_ID, PRINCIPAL);
    user = User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    donation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            CREATED_AT);
    donor =
        Donor.create(
            Id.from(DONOR_ID), Id.from(USER_ID), "user@example.com", "Jane", "Doe", CREATED_AT);
  }

  @Test
  void execute_should_load_donation_by_command_id() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(donor));

    handler.execute(command);

    verify(findDonationByIdRepository).findById(Id.from(DONATION_ID));
  }

  @Test
  void execute_should_throw_donation_not_found_when_donation_is_missing() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID))).thenReturn(Optional.empty());

    DonationNotFoundException exception =
        assertThrows(DonationNotFoundException.class, () -> handler.execute(command));

    assertEquals("Donation not found: " + DONATION_ID, exception.getMessage());
  }

  @Test
  void execute_should_not_call_delete_by_id_when_donation_not_found() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID))).thenReturn(Optional.empty());

    assertThrows(DonationNotFoundException.class, () -> handler.execute(command));

    verifyNoInteractions(donationRepository);
  }

  @Test
  void execute_should_resolve_user_with_command_principal() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(donor));

    handler.execute(command);

    verify(getCurrentAuthUserUseCase).execute(PRINCIPAL);
  }

  @Test
  void execute_should_throw_unauthenticated_when_user_resolution_fails() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL))
        .thenThrow(new UnauthenticatedRequestException());

    assertThrows(UnauthenticatedRequestException.class, () -> handler.execute(command));
    verify(donationRepository, never()).deleteById(any());
  }

  @Test
  void execute_should_throw_donation_forbidden_when_user_has_no_donor() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());

    DonationForbiddenException exception =
        assertThrows(DonationForbiddenException.class, () -> handler.execute(command));

    assertEquals("Donation forbidden: " + DONATION_ID, exception.getMessage());
  }

  @Test
  void execute_should_not_call_delete_by_id_when_user_has_no_donor() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());

    assertThrows(DonationForbiddenException.class, () -> handler.execute(command));

    verify(donationRepository, never()).deleteById(any());
  }

  @Test
  void execute_should_throw_donation_forbidden_when_donor_does_not_match_donation() {
    Donor otherDonor =
        Donor.create(
            Id.from(OTHER_DONOR_ID),
            Id.from(USER_ID),
            "other@example.com",
            "Other",
            "Donor",
            CREATED_AT);
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(otherDonor));

    assertThrows(DonationForbiddenException.class, () -> handler.execute(command));
  }

  @Test
  void execute_should_not_call_delete_by_id_when_donor_does_not_match_donation() {
    Donor otherDonor =
        Donor.create(
            Id.from(OTHER_DONOR_ID),
            Id.from(USER_ID),
            "other@example.com",
            "Other",
            "Donor",
            CREATED_AT);
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(otherDonor));

    assertThrows(DonationForbiddenException.class, () -> handler.execute(command));

    verify(donationRepository, never()).deleteById(any());
  }

  @Test
  void execute_should_call_delete_by_id_when_donor_matches() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(donor));

    handler.execute(command);

    verify(donationRepository).deleteById(Id.from(DONATION_ID));
  }
}
