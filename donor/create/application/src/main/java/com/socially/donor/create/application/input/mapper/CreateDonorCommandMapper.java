package com.socially.donor.create.application.input.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.donor.create.application.input.CreateDonorCommand;
import com.socially.donor.kernel.domain.entity.Donor;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public final class CreateDonorCommandMapper {
  public Donor toDomain(Id donorId, CreateDonorCommand command, Instant now) {
    return Donor.create(
        donorId,
        Id.from(command.userId()),
        command.email(),
        command.givenName(),
        command.familyName(),
        now);
  }
}
