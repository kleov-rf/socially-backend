package com.socially.user.updateprofile.domain.port.right;

import java.util.Optional;

public interface UpdateUserProfileRepository {
  void updateProfile(
      String userId,
      String issuer,
      String subject,
      Optional<String> email,
      Optional<String> givenName,
      Optional<String> familyName);
}
