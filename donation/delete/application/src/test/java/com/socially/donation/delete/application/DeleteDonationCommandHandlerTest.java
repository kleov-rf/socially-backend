package com.socially.donation.delete.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.delete.application.input.DeleteDonationCommand;
import com.socially.donation.delete.domain.port.right.DeleteDonationRepository;
import com.socially.donation.getbyid.domain.port.right.FindDonationByIdRepository;
import com.socially.donation.kernel.application.port.left.AssertDonationOwnedByPrincipalUseCase;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.exception.DonationNotFoundException;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
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
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";

  @Mock private FindDonationByIdRepository findDonationByIdRepository;
  @Mock private AssertDonationOwnedByPrincipalUseCase assertDonationOwnedByPrincipalUseCase;
  @Mock private DeleteDonationRepository donationRepository;
  @InjectMocks private DeleteDonationCommandHandler handler;

  private DeleteDonationCommand command;
  private Donation donation;

  @BeforeEach
  void setUp() {
    command = new DeleteDonationCommand(DONATION_ID, PRINCIPAL);
    donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            CREATED_AT);
  }

  @Test
  void execute_should_load_donation_by_command_id() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));

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

    verifyNoInteractions(donationRepository, assertDonationOwnedByPrincipalUseCase);
  }

  @Test
  void execute_should_call_assert_donation_owned_by_principal_with_donation_and_principal() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));

    handler.execute(command);

    verify(assertDonationOwnedByPrincipalUseCase).execute(eq(donation), eq(PRINCIPAL));
  }

  @Test
  void execute_should_not_call_delete_by_id_when_ownership_assertion_fails() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));
    doThrow(new DonationForbiddenException(DONATION_ID))
        .when(assertDonationOwnedByPrincipalUseCase)
        .execute(donation, PRINCIPAL);

    assertThrows(DonationForbiddenException.class, () -> handler.execute(command));

    verify(donationRepository, never()).deleteById(any());
  }

  @Test
  void execute_should_call_delete_by_id_when_ownership_assertion_succeeds() {
    when(findDonationByIdRepository.findById(Id.from(DONATION_ID)))
        .thenReturn(Optional.of(donation));

    handler.execute(command);

    verify(donationRepository).deleteById(Id.from(DONATION_ID));
  }
}
