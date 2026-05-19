package com.socially.donation.kernel.infrastructure.right.adapter.persistence;

import com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity.DonationEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationEntityRepository extends JpaRepository<DonationEntity, UUID> {

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
      order by d.createdAt desc, d.id desc
      """)
  List<DonationEntity> findByOrderByCreatedAtDescIdDesc(Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
      order by d.createdAt asc, d.id asc
      """)
  List<DonationEntity> findByOrderByCreatedAtAscIdAsc(Pageable pageable);

  long countByDeletedAtIsNull();

  @Query(
      """
      select count(d)
      from DonationEntity d
      where d.deletedAt is null
        and (lower(d.title) like :searchPattern
         or lower(d.description) like :searchPattern)
      """)
  long countBySearchPattern(@Param("searchPattern") String searchPattern);

  Optional<DonationEntity> findByIdAndDeletedAtIsNull(UUID id);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      update DonationEntity d
      set d.deletedAt = :deletedAt
      where d.id = :id
        and d.deletedAt is null
      """)
  int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (lower(d.title) like :searchPattern
         or lower(d.description) like :searchPattern)
      order by d.createdAt desc, d.id desc
      """)
  List<DonationEntity> findBySearchPatternOrderByCreatedAtDescIdDesc(
      @Param("searchPattern") String searchPattern, Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (lower(d.title) like :searchPattern
         or lower(d.description) like :searchPattern)
      order by d.createdAt asc, d.id asc
      """)
  List<DonationEntity> findBySearchPatternOrderByCreatedAtAscIdAsc(
      @Param("searchPattern") String searchPattern, Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (d.createdAt < :createdAt
         or (d.createdAt = :createdAt and d.id < :id))
      order by d.createdAt desc, d.id desc
      """)
  List<DonationEntity> findNextPage(
      @Param("createdAt") Instant createdAt, @Param("id") UUID id, Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (lower(d.title) like :searchPattern
         or lower(d.description) like :searchPattern)
        and (d.createdAt < :createdAt
         or (d.createdAt = :createdAt and d.id < :id))
      order by d.createdAt desc, d.id desc
      """)
  List<DonationEntity> findNextPageBySearchPattern(
      @Param("searchPattern") String searchPattern,
      @Param("createdAt") Instant createdAt,
      @Param("id") UUID id,
      Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (d.createdAt > :createdAt
         or (d.createdAt = :createdAt and d.id > :id))
      order by d.createdAt asc, d.id asc
      """)
  List<DonationEntity> findPreviousPage(
      @Param("createdAt") Instant createdAt, @Param("id") UUID id, Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (lower(d.title) like :searchPattern
         or lower(d.description) like :searchPattern)
        and (d.createdAt > :createdAt
         or (d.createdAt = :createdAt and d.id > :id))
      order by d.createdAt asc, d.id asc
      """)
  List<DonationEntity> findPreviousPageBySearchPattern(
      @Param("searchPattern") String searchPattern,
      @Param("createdAt") Instant createdAt,
      @Param("id") UUID id,
      Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (d.createdAt > :createdAt
         or (d.createdAt = :createdAt and d.id > :id))
      order by d.createdAt asc, d.id asc
      """)
  List<DonationEntity> findNextPageForOldestFirst(
      @Param("createdAt") Instant createdAt, @Param("id") UUID id, Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (lower(d.title) like :searchPattern
         or lower(d.description) like :searchPattern)
        and (d.createdAt > :createdAt
         or (d.createdAt = :createdAt and d.id > :id))
      order by d.createdAt asc, d.id asc
      """)
  List<DonationEntity> findNextPageForOldestFirstBySearchPattern(
      @Param("searchPattern") String searchPattern,
      @Param("createdAt") Instant createdAt,
      @Param("id") UUID id,
      Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (d.createdAt < :createdAt
         or (d.createdAt = :createdAt and d.id < :id))
      order by d.createdAt desc, d.id desc
      """)
  List<DonationEntity> findPreviousPageForOldestFirst(
      @Param("createdAt") Instant createdAt, @Param("id") UUID id, Pageable pageable);

  @Query(
      """
      select d
      from DonationEntity d
      where d.deletedAt is null
        and (lower(d.title) like :searchPattern
         or lower(d.description) like :searchPattern)
        and (d.createdAt < :createdAt
         or (d.createdAt = :createdAt and d.id < :id))
      order by d.createdAt desc, d.id desc
      """)
  List<DonationEntity> findPreviousPageForOldestFirstBySearchPattern(
      @Param("searchPattern") String searchPattern,
      @Param("createdAt") Instant createdAt,
      @Param("id") UUID id,
      Pageable pageable);

  @Query(
      value =
          """
          select d.*
          from donations d
          where d.deleted_at is null
          order by (6371000 * acos(LEAST(1.0, GREATEST(-1.0,
            cos(radians(:refLat)) * cos(radians(d.location_latitude))
            * cos(radians(d.location_longitude) - radians(:refLng))
            + sin(radians(:refLat)) * sin(radians(d.location_latitude))
          )))) asc, d.id asc
          """,
      nativeQuery = true)
  List<DonationEntity> findNearestFirst(
      @Param("refLat") double refLat, @Param("refLng") double refLng, Pageable pageable);

  @Query(
      value =
          """
          select d.*
          from donations d
          where d.deleted_at is null
            and (lower(d.title) like :searchPattern
             or lower(d.description) like :searchPattern)
          order by (6371000 * acos(LEAST(1.0, GREATEST(-1.0,
            cos(radians(:refLat)) * cos(radians(d.location_latitude))
            * cos(radians(d.location_longitude) - radians(:refLng))
            + sin(radians(:refLat)) * sin(radians(d.location_latitude))
          )))) asc, d.id asc
          """,
      nativeQuery = true)
  List<DonationEntity> findNearestFirstBySearchPattern(
      @Param("searchPattern") String searchPattern,
      @Param("refLat") double refLat,
      @Param("refLng") double refLng,
      Pageable pageable);

  @Query(
      value =
          """
          select d.*
          from donations d
          where d.deleted_at is null
            and ((6371000 * acos(LEAST(1.0, GREATEST(-1.0,
              cos(radians(:refLat)) * cos(radians(d.location_latitude))
              * cos(radians(d.location_longitude) - radians(:refLng))
              + sin(radians(:refLat)) * sin(radians(d.location_latitude))
            )))) > :boundaryDistance
             or ((6371000 * acos(LEAST(1.0, GREATEST(-1.0,
              cos(radians(:refLat)) * cos(radians(d.location_latitude))
              * cos(radians(d.location_longitude) - radians(:refLng))
              + sin(radians(:refLat)) * sin(radians(d.location_latitude))
            )))) = :boundaryDistance and d.id > :id))
          order by (6371000 * acos(LEAST(1.0, GREATEST(-1.0,
            cos(radians(:refLat)) * cos(radians(d.location_latitude))
            * cos(radians(d.location_longitude) - radians(:refLng))
            + sin(radians(:refLat)) * sin(radians(d.location_latitude))
          )))) asc, d.id asc
          """,
      nativeQuery = true)
  List<DonationEntity> findNextNearestFirstPage(
      @Param("refLat") double refLat,
      @Param("refLng") double refLng,
      @Param("boundaryDistance") double boundaryDistance,
      @Param("id") UUID id,
      Pageable pageable);

  @Query(
      value =
          """
          select d.*
          from donations d
          where d.deleted_at is null
            and (lower(d.title) like :searchPattern
             or lower(d.description) like :searchPattern)
            and ((6371000 * acos(LEAST(1.0, GREATEST(-1.0,
              cos(radians(:refLat)) * cos(radians(d.location_latitude))
              * cos(radians(d.location_longitude) - radians(:refLng))
              + sin(radians(:refLat)) * sin(radians(d.location_latitude))
            )))) > :boundaryDistance
             or ((6371000 * acos(LEAST(1.0, GREATEST(-1.0,
              cos(radians(:refLat)) * cos(radians(d.location_latitude))
              * cos(radians(d.location_longitude) - radians(:refLng))
              + sin(radians(:refLat)) * sin(radians(d.location_latitude))
            )))) = :boundaryDistance and d.id > :id))
          order by (6371000 * acos(LEAST(1.0, GREATEST(-1.0,
            cos(radians(:refLat)) * cos(radians(d.location_latitude))
            * cos(radians(d.location_longitude) - radians(:refLng))
            + sin(radians(:refLat)) * sin(radians(d.location_latitude))
          )))) asc, d.id asc
          """,
      nativeQuery = true)
  List<DonationEntity> findNextNearestFirstPageBySearchPattern(
      @Param("searchPattern") String searchPattern,
      @Param("refLat") double refLat,
      @Param("refLng") double refLng,
      @Param("boundaryDistance") double boundaryDistance,
      @Param("id") UUID id,
      Pageable pageable);

  @Query(
      value =
          """
          select d.*
          from donations d
          where d.deleted_at is null
            and ((6371000 * acos(LEAST(1.0, GREATEST(-1.0,
              cos(radians(:refLat)) * cos(radians(d.location_latitude))
              * cos(radians(d.location_longitude) - radians(:refLng))
              + sin(radians(:refLat)) * sin(radians(d.location_latitude))
            )))) < :boundaryDistance
             or ((6371000 * acos(LEAST(1.0, GREATEST(-1.0,
              cos(radians(:refLat)) * cos(radians(d.location_latitude))
              * cos(radians(d.location_longitude) - radians(:refLng))
              + sin(radians(:refLat)) * sin(radians(d.location_latitude))
            )))) = :boundaryDistance and d.id < :id))
          order by (6371000 * acos(LEAST(1.0, GREATEST(-1.0,
            cos(radians(:refLat)) * cos(radians(d.location_latitude))
            * cos(radians(d.location_longitude) - radians(:refLng))
            + sin(radians(:refLat)) * sin(radians(d.location_latitude))
          )))) desc, d.id desc
          """,
      nativeQuery = true)
  List<DonationEntity> findPreviousNearestFirstPage(
      @Param("refLat") double refLat,
      @Param("refLng") double refLng,
      @Param("boundaryDistance") double boundaryDistance,
      @Param("id") UUID id,
      Pageable pageable);

  @Query(
      value =
          """
          select d.*
          from donations d
          where d.deleted_at is null
            and (lower(d.title) like :searchPattern
             or lower(d.description) like :searchPattern)
            and ((6371000 * acos(LEAST(1.0, GREATEST(-1.0,
              cos(radians(:refLat)) * cos(radians(d.location_latitude))
              * cos(radians(d.location_longitude) - radians(:refLng))
              + sin(radians(:refLat)) * sin(radians(d.location_latitude))
            )))) < :boundaryDistance
             or ((6371000 * acos(LEAST(1.0, GREATEST(-1.0,
              cos(radians(:refLat)) * cos(radians(d.location_latitude))
              * cos(radians(d.location_longitude) - radians(:refLng))
              + sin(radians(:refLat)) * sin(radians(d.location_latitude))
            )))) = :boundaryDistance and d.id < :id))
          order by (6371000 * acos(LEAST(1.0, GREATEST(-1.0,
            cos(radians(:refLat)) * cos(radians(d.location_latitude))
            * cos(radians(d.location_longitude) - radians(:refLng))
            + sin(radians(:refLat)) * sin(radians(d.location_latitude))
          )))) desc, d.id desc
          """,
      nativeQuery = true)
  List<DonationEntity> findPreviousNearestFirstPageBySearchPattern(
      @Param("searchPattern") String searchPattern,
      @Param("refLat") double refLat,
      @Param("refLng") double refLng,
      @Param("boundaryDistance") double boundaryDistance,
      @Param("id") UUID id,
      Pageable pageable);
}
