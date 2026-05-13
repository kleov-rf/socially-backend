package com.socially.donation.create.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.socially.auth.me.application.exception.UnauthenticatedRequestException;
import com.socially.auth.me.application.port.left.GetCurrentAuthUserUseCase;
import com.socially.donation.create.application.input.CreateDonationCommand;
import com.socially.donation.create.application.input.mapper.CreateDonationCommandMapper;
import com.socially.donation.create.domain.port.right.CreateDonationRepository;
import com.socially.donation.kernel.domain.entity.Donation;
import com.socially.donation.kernel.domain.valueobject.Description;
import com.socially.donation.kernel.domain.valueobject.DonorId;
import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donation.kernel.domain.valueobject.Title;
import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.create.application.port.left.CreateDonorUseCase;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonationCommandHandlerTest {

  private static final String DONATION_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440010";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");
  private static final Principal PRINCIPAL = () -> "user@example.com";

  @Mock private GetCurrentAuthUserUseCase getCurrentAuthUserUseCase;
  @Mock private CreateDonorUseCase createDonorUseCase;
  @Mock private CreateDonationRepository donationRepository;
  @Mock private CreateDonationCommandMapper createDonationCommandMapper;
  @Mock private Clock clock;
  @InjectMocks private CreateDonationCommandHandler handler;

  private CreateDonationCommand command;
  private User user;

  @BeforeEach
  void setUp() {
    command = new CreateDonationCommand(DONATION_ID, "Test Title", "Test Description", PRINCIPAL);
    user =
        User.create(
            Id.from(USER_ID),
            Email.from("user@example.com"),
            "Jane",
            "Doe",
            CREATED_AT);
  }

  @Test
  void execute_should_resolve_user_with_command_principal() {
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(eq(command), any(), eq(CREATED_AT)))
        .thenReturn(
            Donation.create(
                Id.from(DONATION_ID),
                DonorId.from(DONOR_ID),
                Title.from("Test Title"),
                Description.from("Test Description"),
                CREATED_AT,
                CREATED_AT));

    handler.execute(command);

    verify(getCurrentAuthUserUseCase).execute(PRINCIPAL);
  }

  @Test
  void execute_should_call_create_donor_with_resolved_user_data() {
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(eq(command), any(), eq(CREATED_AT)))
        .thenReturn(
            Donation.create(
                Id.from(DONATION_ID),
                DonorId.from(DONOR_ID),
                Title.from("Test Title"),
                Description.from("Test Description"),
                CREATED_AT,
                CREATED_AT));

    handler.execute(command);

    ArgumentCaptor<CreateDonorCommand> donorCommandCaptor =
        ArgumentCaptor.forClass(CreateDonorCommand.class);
    verify(createDonorUseCase).execute(donorCommandCaptor.capture());
    CreateDonorCommand donorCommand = donorCommandCaptor.getValue();
    UUID.fromString(donorCommand.id());
    assertEquals(USER_ID, donorCommand.userId());
    assertEquals("user@example.com", donorCommand.email());
    assertEquals("Jane", donorCommand.givenName());
    assertEquals("Doe", donorCommand.familyName());
  }

  @Test
  void execute_should_call_mapper_with_generated_donor_id_and_clock_instant() {
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(eq(command), any(), eq(CREATED_AT)))
        .thenReturn(
            Donation.create(
                Id.from(DONATION_ID),
                DonorId.from(DONOR_ID),
                Title.from("Test Title"),
                Description.from("Test Description"),
                CREATED_AT,
                CREATED_AT));

    handler.execute(command);

    ArgumentCaptor<String> donorIdCaptor = ArgumentCaptor.forClass(String.class);
    verify(createDonationCommandMapper)
        .toDomain(eq(command), donorIdCaptor.capture(), eq(CREATED_AT));
    UUID.fromString(donorIdCaptor.getValue());
  }

  @Test
  void execute_should_call_repository_create_with_mapped_donation() {
    Donation mappedDonation =
        Donation.create(
            Id.from(DONATION_ID),
            DonorId.from(DONOR_ID),
            Title.from("Test Title"),
            Description.from("Test Description"),
            CREATED_AT,
            CREATED_AT);
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL)).thenReturn(user);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonationCommandMapper.toDomain(eq(command), any(), eq(CREATED_AT)))
        .thenReturn(mappedDonation);

    handler.execute(command);

    verify(donationRepository).create(mappedDonation);
  }

  @Test
  void execute_should_throw_and_stop_when_user_is_unauthenticated() {
    when(getCurrentAuthUserUseCase.execute(PRINCIPAL))
        .thenThrow(new UnauthenticatedRequestException());

    assertThrows(UnauthenticatedRequestException.class, () -> handler.execute(command));
    verifyNoInteractions(createDonorUseCase, createDonationCommandMapper, donationRepository);
  }
}
