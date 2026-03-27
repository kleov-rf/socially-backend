package com.socially.donation.kernel.infrastructure.right.adapter.persistence;

import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationEntityRepository extends JpaRepository<DonationEntity, UUID> {}
