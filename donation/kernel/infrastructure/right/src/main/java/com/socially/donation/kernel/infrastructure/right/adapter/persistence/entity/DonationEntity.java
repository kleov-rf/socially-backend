package com.socially.donation.kernel.infrastructure.right.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
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

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;
}
