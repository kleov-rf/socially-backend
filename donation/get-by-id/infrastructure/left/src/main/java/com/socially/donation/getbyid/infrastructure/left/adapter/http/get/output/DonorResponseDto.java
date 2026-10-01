package com.socially.donation.getbyid.infrastructure.left.adapter.http.get.output;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record DonorResponseDto(
    String id, String email, Optional<String> givenName, Optional<String> familyName) {

  public static DonorResponseDto create(String id, String email) {
    return new DonorResponseDto(id, email, Optional.empty(), Optional.empty());
  }

  public DonorResponseDto withGivenName(Optional<String> givenName) {
    return new DonorResponseDto(id, email, givenName, familyName);
  }

  public DonorResponseDto withFamilyName(Optional<String> familyName) {
    return new DonorResponseDto(id, email, givenName, familyName);
  }
}
