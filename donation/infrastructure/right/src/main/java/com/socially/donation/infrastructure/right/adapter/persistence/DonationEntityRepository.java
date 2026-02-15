package com.socially.donation.infrastructure.right.adapter.persistence;

import com.socially.donation.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface DonationEntityRepository extends JpaRepository<DonationEntity, UUID> {}
