package com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "donations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonationEntity {
  @Id private UUID id;

  @Column(name = "donor_id", nullable = false)
  private UUID donorId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "last_updated_at", nullable = false)
  private Instant lastUpdatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @Column(name = "location_address", nullable = false)
  private String locationAddress;

  @Column(name = "location_latitude", nullable = false)
  private Double locationLatitude;

  @Column(name = "location_longitude", nullable = false)
  private Double locationLongitude;

  @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DonationImageEntity> images = new ArrayList<>();

  public static DonationEntity create(
      UUID id,
      UUID donorId,
      String title,
      String description,
      Instant createdAt,
      Instant lastUpdatedAt,
      String locationAddress,
      Double locationLatitude,
      Double locationLongitude) {
    return new DonationEntity(
        id,
        donorId,
        title,
        description,
        createdAt,
        lastUpdatedAt,
        null,
        locationAddress,
        locationLatitude,
        locationLongitude,
        new ArrayList<>());
  }
}
