package com.socially.user.kernel.infrastructure.right.adapter.persistence.entity;

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
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
  @Id @Getter private UUID id;

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

  public static UserEntity create(UUID id, String email, Instant createdAt) {
    return new UserEntity(id, email, null, null, createdAt);
  }

  public UserEntity withGivenName(Optional<String> givenName) {
    return new UserEntity(id, email, givenName.orElse(null), familyName, createdAt);
  }

  public UserEntity withFamilyName(Optional<String> familyName) {
    return new UserEntity(id, email, givenName, familyName.orElse(null), createdAt);
  }

  public Optional<String> givenName() {
    return Optional.ofNullable(givenName);
  }

  public Optional<String> familyName() {
    return Optional.ofNullable(familyName);
  }
}
