package com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper;

import com.socially.commons.kernel.domain.valueobject.Id;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public final class UserEntityMapper {

  public UserEntity toEntity(User user) {
    return UserEntity.create(user.id().value(), user.email().value(), user.createdAt())
        .withGivenName(user.givenName())
        .withFamilyName(user.familyName());
  }

  public User toDomain(UserEntity entity) {
    return User.create(
            Id.from(entity.getId().toString()),
            Email.from(entity.getEmail()),
            entity.getCreatedAt())
        .withGivenName(entity.givenName())
        .withFamilyName(entity.familyName());
  }
}
