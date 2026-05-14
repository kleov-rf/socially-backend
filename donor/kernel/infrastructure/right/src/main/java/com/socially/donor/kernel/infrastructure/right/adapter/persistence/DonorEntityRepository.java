package com.socially.donor.kernel.infrastructure.right.adapter.persistence;

import com.socially.donor.kernel.infrastructure.right.adapter.persistence.entity.DonorEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorEntityRepository extends JpaRepository<DonorEntity, UUID> {

  Optional<DonorEntity> findByUserId(UUID userId);
}
