package com.socially.donation.find.application.output;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FindDonorSummaryDto(
    String id, Optional<String> givenName, Optional<String> familyName) {

  public static FindDonorSummaryDto create(String id) {
    return new FindDonorSummaryDto(id, Optional.empty(), Optional.empty());
  }

  public FindDonorSummaryDto withGivenName(Optional<String> givenName) {
    return new FindDonorSummaryDto(id, givenName, familyName);
  }

  public FindDonorSummaryDto withFamilyName(Optional<String> familyName) {
    return new FindDonorSummaryDto(id, givenName, familyName);
  }
}
