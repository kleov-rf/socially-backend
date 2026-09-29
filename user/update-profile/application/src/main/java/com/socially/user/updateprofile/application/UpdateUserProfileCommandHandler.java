package com.socially.user.updateprofile.application;

import com.socially.user.updateprofile.application.input.UpdateUserProfileCommand;
import com.socially.user.updateprofile.application.port.left.UpdateUserProfileUseCase;
import com.socially.user.updateprofile.domain.port.right.UpdateUserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public final class UpdateUserProfileCommandHandler implements UpdateUserProfileUseCase {

  private final UpdateUserProfileRepository updateUserProfileRepository;

  @Override
  public void execute(UpdateUserProfileCommand command) {
    requireText(command.userId(), "userId");
    requireText(command.issuer(), "issuer");
    requireText(command.subject(), "subject");
    updateUserProfileRepository.updateProfile(
        command.userId().trim(),
        command.issuer().trim(),
        command.subject().trim(),
        command.email().filter(StringUtils::hasText).map(String::trim),
        command.givenName().filter(StringUtils::hasText).map(String::trim),
        command.familyName().filter(StringUtils::hasText).map(String::trim));
  }

  private static void requireText(String value, String fieldName) {
    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException(fieldName + " cannot be blank");
    }
  }
}
