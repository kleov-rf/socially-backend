package com.socially.user.create.infrastructure.right.adapter.persistence;

import com.socially.user.create.domain.port.right.CreateUserRepository;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCreateUserRepository implements CreateUserRepository {

  private final UserEntityRepository entityRepository;
  private final UserEntityMapper entityMapper;

  @Override
  public void create(User user) {
    if (entityRepository.existsByEmail(user.email().value())) {
      return;
    }
    entityRepository.save(entityMapper.toEntity(user));
  }
}
