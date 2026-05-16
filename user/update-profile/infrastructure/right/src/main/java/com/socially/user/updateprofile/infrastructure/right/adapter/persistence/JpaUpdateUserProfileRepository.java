package com.socially.user.updateprofile.infrastructure.right.adapter.persistence;

import com.socially.user.kernel.infrastructure.right.adapter.persistence.FederatedIdentityEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.FederatedIdentityEntity;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity;
import com.socially.user.updateprofile.domain.port.right.UpdateUserProfileRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaUpdateUserProfileRepository implements UpdateUserProfileRepository {

  private final UserEntityRepository userEntityRepository;
  private final FederatedIdentityEntityRepository federatedIdentityEntityRepository;

  @Override
  public void updateProfile(
      String userId,
      String issuer,
      String subject,
      String email,
      String givenName,
      String familyName) {
    UUID userUuid = UUID.fromString(userId);
    UserEntity userEntity =
        userEntityRepository
            .findById(userUuid)
            .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

    UserEntity updatedUser =
        UserEntity.create(
            userEntity.getId(),
            email != null ? email : userEntity.getEmail(),
            givenName != null ? givenName : userEntity.getGivenName(),
            familyName != null ? familyName : userEntity.getFamilyName(),
            userEntity.getCreatedAt());
    userEntityRepository.save(updatedUser);

    FederatedIdentityEntity federatedIdentity =
        federatedIdentityEntityRepository
            .findByIssuerAndSubject(issuer, subject)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Federated identity not found for issuer=%s subject=%s"
                            .formatted(issuer, subject)));

    FederatedIdentityEntity updatedFederatedIdentity =
        FederatedIdentityEntity.create(
            federatedIdentity.getId(),
            federatedIdentity.getUserId(),
            federatedIdentity.getIssuer(),
            federatedIdentity.getSubject(),
            email != null ? email : federatedIdentity.getEmail(),
            federatedIdentity.getCreatedAt());
    federatedIdentityEntityRepository.save(updatedFederatedIdentity);
  }
}
