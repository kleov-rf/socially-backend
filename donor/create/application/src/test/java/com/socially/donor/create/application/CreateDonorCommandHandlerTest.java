package com.socially.donor.create.application;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.create.application.input.mapper.CreateDonorCommandMapper;
import com.socially.donor.create.domain.port.right.CreateDonorRepository;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.domain.valueobject.Id;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
  @Mock private Clock clock;

  @InjectMocks private CreateDonorCommandHandler handler;

  @Test
  void execute_should_call_mapper_with_received_command_and_clock_instant() {
    var command = new CreateDonorCommand(DONOR_ID, USER_ID, "donor@example.com", "Jane", "Doe");
    Donor mappedDonor =
        Donor.create(
            Id.from(DONOR_ID),
            UUID.fromString(USER_ID),
            "donor@example.com",
            "Jane",
            "Doe",
            CREATED_AT);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonorCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedDonor);

    handler.execute(command);

    verify(createDonorCommandMapper).toDomain(command, CREATED_AT);
  }

  @Test
  void execute_should_call_clock_instant() {
    var command = new CreateDonorCommand(DONOR_ID, USER_ID, "donor@example.com", "Jane", "Doe");
    Donor mappedDonor =
        Donor.create(
            Id.from(DONOR_ID),
            UUID.fromString(USER_ID),
            "donor@example.com",
            "Jane",
            "Doe",
            CREATED_AT);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonorCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedDonor);

    handler.execute(command);

    verify(clock).instant();
  }

  @Test
  void execute_should_call_repository_create_with_mapped_donor() {
    var command = new CreateDonorCommand(DONOR_ID, USER_ID, "donor@example.com", "Jane", "Doe");
    Donor mappedDonor =
        Donor.create(
            Id.from(DONOR_ID),
            UUID.fromString(USER_ID),
            "donor@example.com",
            "Jane",
            "Doe",
            CREATED_AT);
    when(clock.instant()).thenReturn(CREATED_AT);
    when(createDonorCommandMapper.toDomain(command, CREATED_AT)).thenReturn(mappedDonor);

    handler.execute(command);

    verify(donorRepository).create(mappedDonor);
  }
}
