package com.socially.donation.kernel.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.application.port.left.GetAuthenticatedUserUseCase;
import com.socially.auth.kernel.domain.exception.AuthUnauthorizedException;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.exception.DonationForbiddenException;
import com.socially.donation.kernel.domain.valueobject.Description;
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
class AssertDonationOwnedByPrincipalCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String OTHER_DONOR_ID = "550e8400-e29b-41d4-a716-446655440002";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";

  @Mock private GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
  @Mock private FindDonorByUserIdUseCase findDonorByUserIdUseCase;

  @InjectMocks private AssertDonationOwnedByPrincipalCommandHandler handler;

  private User user;
  private Donation donation;
  private Donor donor;

  @BeforeEach
  void setUp() {
    user = User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    donation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            CREATED_AT);
    donor =
        Donor.create(
            Id.from(DONOR_ID), Id.from(USER_ID), "user@example.com", "Jane", "Doe", CREATED_AT);
  }

  @Test
  void execute_should_not_throw_when_donor_owns_donation() {
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(donor));

    assertDoesNotThrow(() -> handler.execute(donation, PRINCIPAL));
  }

  @Test
  void execute_should_throw_donation_forbidden_when_user_has_no_donor() {
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());

    DonationForbiddenException exception =
        assertThrows(DonationForbiddenException.class, () -> handler.execute(donation, PRINCIPAL));

    assertEquals("Donation forbidden: " + DONATION_ID, exception.getMessage());
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
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(otherDonor));

    DonationForbiddenException exception =
        assertThrows(DonationForbiddenException.class, () -> handler.execute(donation, PRINCIPAL));

    assertEquals("Donation forbidden: " + DONATION_ID, exception.getMessage());
  }

  @Test
  void execute_should_throw_unauthenticated_when_user_resolution_fails() {
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL))
        .thenThrow(new AuthUnauthorizedException("Unauthenticated request"));

    AuthUnauthorizedException exception =
        assertThrows(AuthUnauthorizedException.class, () -> handler.execute(donation, PRINCIPAL));

    assertEquals("Unauthenticated request", exception.getMessage());
  }
}
