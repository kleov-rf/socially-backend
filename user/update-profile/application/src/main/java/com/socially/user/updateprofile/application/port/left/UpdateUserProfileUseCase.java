package com.socially.user.updateprofile.application.port.left;

import com.socially.user.kernel.domain.entity.User;
import com.socially.user.updateprofile.application.input.UpdateUserProfileCommand;

public interface UpdateUserProfileUseCase {
  User execute(UpdateUserProfileCommand command);
}
