package com.socially.user.kernel.infrastructure.right.adapter.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "federated_identities",
    uniqueConstraints =
        @UniqueConstraint(
            name = "federated_identities_issuer_subject_unique",
            columnNames = {"issuer", "subject"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FederatedIdentityEntity {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "issuer", nullable = false)
  private String issuer;

  @Column(name = "subject", nullable = false)
  private String subject;

  @Column(name = "email")
  private String email;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public static FederatedIdentityEntity create(
      UUID id, UUID userId, String issuer, String subject, String email, Instant createdAt) {
    return new FederatedIdentityEntity(id, userId, issuer, subject, email, createdAt);
  }
}
