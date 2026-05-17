package com.socially.donation.create.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.port.left.GetAuthenticatedUserUseCase;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.mapper.CreateDonationCommandMapper;
import com.socially.donation.create.domain.port.right.CreateDonationRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.domain.exception.DonorNotFoundAfterCreateException;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  public static final Donor DONOR =
      Donor.create(
          Id.from("550e8400-e29b-41d4-a716-446655440001"),
          Id.from(USER_ID),
          "user@example.com",
          "Jane",
          "Doe",
          CREATED_AT);
  private static final Principal PRINCIPAL = () -> "user@example.com";

  @Mock private GetAuthenticatedUserUseCase getAuthenticatedUserUseCase;
  @Mock private FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  @Mock private CreateDonationRepository donationRepository;
  @Mock private CreateDonationCommandMapper createDonationCommandMapper;
  @Mock private Clock clock;
  @InjectMocks private CreateDonationCommandHandler handler;

  private CreateDonationCommand command;
  private User user;
  private Donor donorAfterCreate;

  @BeforeEach
  void setUp() {
    command = new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description", PRINCIPAL);
    user = User.create(Id.from(USER_ID), Email.from("user@example.com"), "Jane", "Doe", CREATED_AT);
    donorAfterCreate = DONOR;
    lenient()
        .when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(donorAfterCreate));
  }

  @Test
  void execute_should_resolve_user_with_command_principal() {
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(eq(command), eq(DONOR), eq(CREATED_AT)))
        .thenReturn(
            Donation.create(
                Id.from(DONATION_ID),
                Id.from("550e8400-e29b-41d4-a716-446655440001"),
                Title.from("Test Title"),
                Description.from("Test Description"),
                CREATED_AT,
                CREATED_AT));

    handler.execute(command);

    verify(getAuthenticatedUserUseCase).execute(PRINCIPAL);
    verify(findDonorByUserIdUseCase).execute(new FindDonorByUserIdQuery(USER_ID));
  }

  @Test
  void execute_should_call_mapper_with_donor_id_from_find_result() {
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(eq(command), eq(DONOR), eq(CREATED_AT)))
        .thenReturn(
            Donation.create(
                Id.from(DONATION_ID),
                Id.from("550e8400-e29b-41d4-a716-446655440001"),
                Title.from("Test Title"),
                Description.from("Test Description"),
                CREATED_AT,
                CREATED_AT));

    handler.execute(command);

    verify(createDonationCommandMapper).toDomain(eq(command), eq(DONOR), eq(CREATED_AT));
  }

  @Test
  void execute_should_call_repository_create_with_mapped_donation() {
    Donation mappedDonation =
        Donation.create(
            Id.from(DONATION_ID),
            Id.from("550e8400-e29b-41d4-a716-446655440001"),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            CREATED_AT);
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(eq(command), eq(DONOR), eq(CREATED_AT)))
        .thenReturn(mappedDonation);

    handler.execute(command);

    verify(donationRepository).create(mappedDonation);
  }

  @Test
  void execute_should_call_mapper_with_existing_donor_id_when_existing_donor_found() {
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(donorAfterCreate));
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(eq(command), eq(DONOR), eq(CREATED_AT)))
        .thenReturn(
            Donation.create(
                Id.from(DONATION_ID),
                Id.from("550e8400-e29b-41d4-a716-446655440001"),
                Title.from("Test Title"),
                Description.from("Test Description"),
                CREATED_AT,
                CREATED_AT));

    handler.execute(command);

    verify(createDonationCommandMapper).toDomain(eq(command), eq(DONOR), eq(CREATED_AT));
  }

  @Test
  void execute_should_throw_when_donor_not_found_after_create() {
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());

    DonorNotFoundAfterCreateException exception =
        assertThrows(DonorNotFoundAfterCreateException.class, () -> handler.execute(command));

    assertEquals("Donor not found after create", exception.getMessage());
    verifyNoInteractions(createDonationCommandMapper, donationRepository);
  }

  @Test
  void execute_should_throw_and_stop_when_user_is_unauthenticated() {
    when(getAuthenticatedUserUseCase.execute(PRINCIPAL))
        .thenThrow(new UnauthenticatedRequestException());

    assertThrows(UnauthenticatedRequestException.class, () -> handler.execute(command));
    verifyNoInteractions(findDonorByUserIdUseCase, createDonationCommandMapper, donationRepository);
  }
}
