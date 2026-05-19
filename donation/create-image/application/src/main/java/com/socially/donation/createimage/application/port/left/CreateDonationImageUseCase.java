package com.socially.donation.createimage.application.port.left;

import com.socially.donation.createimage.application.input.CreateDonationImageCommand;
import com.socially.donation.createimage.application.output.CreateDonationImageResult;

/**
 * Inbound port for requesting a donation image upload.
 *
 * <p>This interface defines the contract between the infrastructure layer (adapters) and the
 * application layer. Adapters like HTTP controllers depend on this interface, not the concrete
 * implementation.
 */
public interface CreateDonationImageUseCase {

  /**
   * Creates donation image metadata and returns pre-signed upload details.
   *
   * @param command the command containing donation and file metadata
   * @return upload and media URLs for the client
   */
  CreateDonationImageResult execute(CreateDonationImageCommand command);
}
