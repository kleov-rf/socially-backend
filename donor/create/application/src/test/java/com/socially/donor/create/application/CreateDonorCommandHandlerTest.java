package com.socially.donor.create.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.create.application.input.mapper.CreateDonorCommandMapper;
import com.socially.donor.create.domain.port.right.CreateDonorRepository;
import com.socially.donor.findbyuserid.application.input.FindDonorByUserIdQuery;
import com.socially.donor.findbyuserid.application.port.left.FindDonorByUserIdUseCase;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDonorCommandHandlerTest {

  private static final String DONOR_ID = "550e8400-e29b-41d4-a716-446655440000";
  private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440001";
  private static final Instant CREATED_AT = Instant.parse("2024-06-01T12:00:00Z");

  @Mock private CreateDonorRepository donorRepository;
  @Mock private CreateDonorCommandMapper createDonorCommandMapper;
  @Mock private FindDonorByUserIdUseCase findDonorByUserIdUseCase;
  @Mock private Clock clock;

  @InjectMocks private CreateDonorCommandHandler handler;

  private static CreateDonorCommand sampleCommand() {
    return CreateDonorCommand.create(USER_ID, "donor@example.com")
        .withGivenName(Optional.of("Jane"))
        .withFamilyName(Optional.of("Doe"));
  }

  private static Donor sampleDonor() {
    return Donor.create(Id.from(DONOR_ID), Id.from(USER_ID), "donor@example.com", CREATED_AT)
        .withGivenName(Optional.of("Jane"))
        .withFamilyName(Optional.of("Doe"));
  }

  @Test
  void execute_should_call_find_donor_by_user_id_with_command_user_id() {
    var command = sampleCommand();
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());

    handler.execute(command);

    verify(findDonorByUserIdUseCase).execute(new FindDonorByUserIdQuery(USER_ID));
  }

  @Test
  void execute_should_not_call_repository_create_when_donor_already_exists() {
    var command = sampleCommand();
    Donor existingDonor = sampleDonor();
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(existingDonor));

    handler.execute(command);

    verify(donorRepository, never()).create(any());
  }

  @Test
  void execute_should_not_call_command_mapper_when_donor_already_exists() {
    var command = sampleCommand();
    Donor existingDonor = sampleDonor();
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.of(existingDonor));

    handler.execute(command);

    verify(createDonorCommandMapper, never()).toDomain(any(), any(), any());
  }

  @Test
  void execute_should_call_mapper_with_generated_donor_id() {
    var command = sampleCommand();
    Donor mappedDonor = sampleDonor();
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonorCommandMapper.toDomain(any(Id.class), eq(command), eq(CREATED_AT)))
        .thenReturn(mappedDonor);

    handler.execute(command);

    ArgumentCaptor<Id> donorIdCaptor = ArgumentCaptor.forClass(Id.class);
    verify(createDonorCommandMapper).toDomain(donorIdCaptor.capture(), eq(command), eq(CREATED_AT));
    donorIdCaptor.getValue().value();
  }

  @Test
  void execute_should_call_mapper_with_received_command_and_clock_instant() {
    var command = sampleCommand();
    Donor mappedDonor = sampleDonor();
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonorCommandMapper.toDomain(any(Id.class), eq(command), eq(CREATED_AT)))
        .thenReturn(mappedDonor);

    handler.execute(command);

    verify(createDonorCommandMapper).toDomain(any(Id.class), eq(command), eq(CREATED_AT));
  }

  @Test
  void execute_should_call_clock_instant() {
    var command = sampleCommand();
    Donor mappedDonor = sampleDonor();
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonorCommandMapper.toDomain(any(Id.class), eq(command), eq(CREATED_AT)))
        .thenReturn(mappedDonor);

    handler.execute(command);

    verify(clock).instant();
  }

  @Test
  void execute_should_call_repository_create_with_mapped_donor() {
    var command = sampleCommand();
    Donor mappedDonor = sampleDonor();
    when(findDonorByUserIdUseCase.execute(new FindDonorByUserIdQuery(USER_ID)))
        .thenReturn(Optional.empty());
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonorCommandMapper.toDomain(any(Id.class), eq(command), eq(CREATED_AT)))
        .thenReturn(mappedDonor);

    handler.execute(command);

    verify(donorRepository).create(mappedDonor);
  }
}
