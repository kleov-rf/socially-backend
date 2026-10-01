package com.socially.donation.find.infrastructure.left.adapter.http.find.output;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record FindDonorResponse(
    String id, Optional<String> givenName, Optional<String> familyName) {

  public static FindDonorResponse create(String id) {
    return new FindDonorResponse(id, Optional.empty(), Optional.empty());
  }

  public FindDonorResponse withGivenName(Optional<String> givenName) {
    return new FindDonorResponse(id, givenName, familyName);
  }

  public FindDonorResponse withFamilyName(Optional<String> familyName) {
    return new FindDonorResponse(id, givenName, familyName);
  }
}
