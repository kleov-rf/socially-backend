package com.socially.donation.application.port.left;

import com.socially.donation.application.get.input.FindDonationByIdQuery;
import com.socially.donation.application.get.output.DonationDto;
import java.util.Optional;

/**
 * Inbound port for finding a donation by its ID.
 *
 * <p>This interface defines the contract between the infrastructure layer (adapters) and the
 * application layer. Adapters like HTTP controllers depend on this interface, not the concrete
 * implementation.
 */
public interface FindDonationByIdUseCase {

  /**
   * Finds a donation by its unique identifier.
   *
   * @param query the query containing the donation ID
   * @return an Optional containing the donation DTO if found, empty otherwise
   */
  Optional<DonationDto> execute(FindDonationByIdQuery query);
}
