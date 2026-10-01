package com.socially.donation.getbyid.application.output;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record DonorSummaryDto(
    String id, String email, Optional<String> givenName, Optional<String> familyName) {

  public static DonorSummaryDto create(String id, String email) {
    return new DonorSummaryDto(id, email, Optional.empty(), Optional.empty());
  }

  public DonorSummaryDto withGivenName(Optional<String> givenName) {
    return new DonorSummaryDto(id, email, givenName, familyName);
  }

  public DonorSummaryDto withFamilyName(Optional<String> familyName) {
    return new DonorSummaryDto(id, email, givenName, familyName);
  }
}
