package com.socially.user.findbyemail.application.port.left;

import com.socially.user.findbyemail.application.input.FindUserByEmailQuery;
import com.socially.user.kernel.domain.entity.User;
import java.util.Optional;

public interface FindUserByEmailUseCase {
  Optional<User> execute(FindUserByEmailQuery query);
}
