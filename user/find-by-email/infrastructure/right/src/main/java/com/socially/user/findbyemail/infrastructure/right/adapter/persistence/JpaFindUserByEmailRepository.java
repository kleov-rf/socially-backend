package com.socially.user.findbyemail.infrastructure.right.adapter.persistence;

import com.socially.user.findbyemail.domain.port.right.FindUserByEmailRepository;
import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.UserEntityRepository;
import com.socially.user.kernel.infrastructure.right.adapter.persistence.mapper.UserEntityMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaFindUserByEmailRepository implements FindUserByEmailRepository {

  private final UserEntityRepository entityRepository;
  private final UserEntityMapper entityMapper;

  @Override
  public Optional<User> findByEmail(Email email) {
    return entityRepository.findByEmail(email.value()).map(entityMapper::toDomain);
  }
}
