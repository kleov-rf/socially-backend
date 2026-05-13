package com.socially.user.create.domain.port.right;

import com.socially.user.kernel.domain.entity.User;

public interface CreateUserRepository {
  User create(User user);
}
