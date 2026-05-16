package com.socially.user.findbyfederatedidentity.infrastructure.right.adapter.persistence;

import com.socially.user.findbyfederatedidentity.domain.port.right.FindUserByFederatedIdentityRepository;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.FederatedIdentityEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper.UserEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaFindUserByFederatedIdentityRepository
    implements FindUserByFederatedIdentityRepository {

  private final FederatedIdentityEntityRepository federatedIdentityEntityRepository;
  private final UserEntityRepository userEntityRepository;
  private final UserEntityMapper userEntityMapper;

  @Override
  public Optional<User> findByIssuerAndSubject(String issuer, String subject) {
    return federatedIdentityEntityRepository
        .findByIssuerAndSubject(issuer, subject)
        .flatMap(
            federatedIdentity ->
                userEntityRepository
                    .findById(federatedIdentity.getUserId())
                    .map(userEntityMapper::toDomain));
  }
}
