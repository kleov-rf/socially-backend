package com.socially.user.kernel.infrastructure.right.adapter.persistence;

import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.FederatedIdentityEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FederatedIdentityEntityRepository
    extends JpaRepository<FederatedIdentityEntity, UUID> {

  Optional<FederatedIdentityEntity> findByIssuerAndSubject(String issuer, String subject);
}
