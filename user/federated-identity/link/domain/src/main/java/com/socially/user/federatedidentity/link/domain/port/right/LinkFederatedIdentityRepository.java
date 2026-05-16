package com.socially.user.federatedidentity.link.domain.port.right;

public interface LinkFederatedIdentityRepository {
  void link(String userId, String issuer, String subject, String email);
}
