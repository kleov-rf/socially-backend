package com.socially.donation.kernel.infrastructure.right.adapter.persistence;

import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationEntityRepository extends JpaRepository<DonationEntity, UUID> {

  List<DonationEntity> findByOrderByCreatedAtDescIdDesc(Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.createdAt < :createdAt
         or (d.createdAt = :createdAt and d.id < :id)
      order by d.createdAt desc, d.id desc
      """)
  List<DonationEntity> findNextPage(
      @Param("createdAt") Instant createdAt, @Param("id") UUID id, Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.createdAt > :createdAt
         or (d.createdAt = :createdAt and d.id > :id)
      order by d.createdAt asc, d.id asc
      """)
  List<DonationEntity> findPreviousPage(
      @Param("createdAt") Instant createdAt, @Param("id") UUID id, Pageable pageable);
}
