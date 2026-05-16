package com.socially.user.federatedidentity.link.infrastructure.right.adapter.persistence;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.federatedidentity.link.domain.port.right.LinkFederatedIdentityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.FederatedIdentityEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.FederatedIdentityEntity;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaLinkFederatedIdentityRepository implements LinkFederatedIdentityRepository {

  private final FederatedIdentityEntityRepository federatedIdentityEntityRepository;
  private final Clock clock;

  @Override
  public void link(String userId, String issuer, String subject, String email) {
    FederatedIdentityEntity entity =
        FederatedIdentityEntity.create(
            Id.generate().value(),
            UUID.fromString(userId),
            issuer,
            subject,
            email,
            clock.instant());
    federatedIdentityEntityRepository.save(entity);
  }
}
