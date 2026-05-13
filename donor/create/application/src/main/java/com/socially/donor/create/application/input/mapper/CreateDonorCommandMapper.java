package com.socially.donor.create.application.input.mapper;

import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.kernel.domain.entity.Donor;
import com.socially.donor.kernel.domain.valueobject.Id;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public final class CreateDonorCommandMapper {
  public Donor toDomain(CreateDonorCommand command, Instant now) {
    return Donor.create(
        Id.from(command.id()),
        UUID.fromString(command.userId()),
        command.email(),
        command.givenName(),
        command.familyName(),
        now);
  }
}
