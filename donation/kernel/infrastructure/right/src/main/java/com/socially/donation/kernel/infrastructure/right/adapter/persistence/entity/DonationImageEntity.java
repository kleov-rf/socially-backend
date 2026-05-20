package com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "donation_images")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonationImageEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "donation_id", nullable = false)
  private DonationEntity donation;

  @Column(name = "storage_object_key", nullable = false)
  private String storageObjectKey;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private Long sizeBytes;

  @Column(name = "primary_flag", nullable = false)
  private Boolean primaryFlag;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public static DonationImageEntity create(
      UUID id,
      String storageObjectKey,
      String contentType,
      Long sizeBytes,
      Boolean primaryFlag,
      Instant createdAt) {
    return new DonationImageEntity(
        id, null, storageObjectKey, contentType, sizeBytes, primaryFlag, createdAt);
  }

  public void attachTo(DonationEntity donationEntity) {
    this.donation = donationEntity;
  }
}
