package com.socially.user.updateprofile.domain.port.right;

public interface UpdateUserProfileRepository {
  void updateProfile(
      String userId,
      String issuer,
      String subject,
      String email,
      String givenName,
      String familyName);
}
