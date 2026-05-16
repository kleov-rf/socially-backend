package com.socially.user.federatedidentity.link.application;

import com.socially.user.federatedidentity.link.application.input.LinkFederatedIdentityCommand;
import com.socially.user.federatedidentity.link.application.port.left.LinkFederatedIdentityUseCase;
import com.socially.user.federatedidentity.link.domain.port.right.LinkFederatedIdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public final class LinkFederatedIdentityCommandHandler implements LinkFederatedIdentityUseCase {

  private final LinkFederatedIdentityRepository linkFederatedIdentityRepository;

  @Override
  public void execute(LinkFederatedIdentityCommand command) {
    requireText(command.userId(), "userId");
    requireText(command.issuer(), "issuer");
    requireText(command.subject(), "subject");
    linkFederatedIdentityRepository.link(
        command.userId().trim(),
        command.issuer().trim(),
        command.subject().trim(),
        blankToNull(command.email()));
  }

  private static void requireText(String value, String fieldName) {
    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException(fieldName + " cannot be blank");
    }
  }

  private static String blankToNull(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
