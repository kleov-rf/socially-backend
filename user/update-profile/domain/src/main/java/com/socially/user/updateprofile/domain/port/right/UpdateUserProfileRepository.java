package com.socially.user.updateprofile.domain.port.right;

import com.socially.user.kernel.domain.entity.User;

public interface UpdateUserProfileRepository {
  void updateProfile(
      String userId, String issuer, String subject, String email, String givenName, String familyName);

  User findById(String userId);
}
