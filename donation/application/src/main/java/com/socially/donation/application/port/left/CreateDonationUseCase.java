package com.socially.donation.application.port.left;

import com.socially.donation.application.create.input.CreateDonationCommand;

/**
 * Inbound port for creating a new donation.
 *
 * <p>This interface defines the contract between the infrastructure layer (adapters) and the
 * application layer. Adapters like HTTP controllers depend on this interface, not the concrete
 * implementation.
 */
public interface CreateDonationUseCase {

  /**
   * Creates a new donation.
   *
   * @param command the command containing the donation data
   */
  void execute(CreateDonationCommand command);
}
