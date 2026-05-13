package com.socially.donor.kernel.infrastructure.right.adapter.persistence.entity;

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
@Table(name = "donors")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DonorEntity {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "given_name")
  private String givenName;

  @Column(name = "family_name")
  private String familyName;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public static DonorEntity create(
      UUID id, UUID userId, String email, String givenName, String familyName, Instant createdAt) {
    return new DonorEntity(id, userId, email, givenName, familyName, createdAt);
  }
}
