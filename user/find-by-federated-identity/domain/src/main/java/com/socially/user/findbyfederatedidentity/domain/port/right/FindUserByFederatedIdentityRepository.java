package com.socially.user.findbyfederatedidentity.domain.port.right;

import com.socially.user.kernel.domain.entity.User;
import java.util.Optional;

public interface FindUserByFederatedIdentityRepository {
  Optional<User> findByIssuerAndSubject(String issuer, String subject);
}
