package com.socially.donor.kernel.infrastructure.right.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "donors")
@NoArgsConstructor
@AllArgsConstructor
public class DonorEntity {
  @Id @Getter private UUID id;

  @Column(name = "user_id", nullable = false)
  @Getter
  private UUID userId;

  @Column(name = "email", nullable = false)
  @Getter
  private String email;

  @Column(name = "given_name")
  private String givenName;

  @Column(name = "family_name")
  private String familyName;

  @Column(name = "created_at", nullable = false)
  @Getter
  private Instant createdAt;

  public static DonorEntity create(UUID id, UUID userId, String email, Instant createdAt) {
    return new DonorEntity(id, userId, email, null, null, createdAt);
  }

  public DonorEntity withGivenName(Optional<String> givenName) {
    return new DonorEntity(id, userId, email, givenName.orElse(null), familyName, createdAt);
  }

  public DonorEntity withFamilyName(Optional<String> familyName) {
    return new DonorEntity(id, userId, email, givenName, familyName.orElse(null), createdAt);
  }

  public Optional<String> givenName() {
    return Optional.ofNullable(givenName);
  }

  public Optional<String> familyName() {
    return Optional.ofNullable(familyName);
  }
}
