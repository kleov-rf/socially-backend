package com.socially.user.federatedidentity.link.application.port.left;

import com.socially.user.federatedidentity.link.application.input.LinkFederatedIdentityCommand;

public interface LinkFederatedIdentityUseCase {
  void execute(LinkFederatedIdentityCommand command);
}
