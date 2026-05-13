package com.socially.donor.create.application.port.left;

import com.socially.donor.create.application.input.CreateDonorCommand;

public interface CreateDonorUseCase {
  void execute(CreateDonorCommand command);
}
