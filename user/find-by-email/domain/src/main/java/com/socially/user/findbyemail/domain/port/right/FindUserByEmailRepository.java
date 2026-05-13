package com.socially.user.findbyemail.domain.port.right;

import com.socially.user.kernel.domain.entity.User;
import com.socially.user.kernel.domain.valueobject.Email;
import java.util.Optional;

public interface FindUserByEmailRepository {
  Optional<User> findByEmail(Email email);
}
