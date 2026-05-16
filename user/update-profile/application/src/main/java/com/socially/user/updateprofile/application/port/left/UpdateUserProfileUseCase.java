package com.socially.user.updateprofile.application.port.left;

import com.socially.user.updateprofile.application.input.UpdateUserProfileCommand;

public interface UpdateUserProfileUseCase {
  void execute(UpdateUserProfileCommand command);
}
