package com.socially.user.findbyfederatedidentity.application;

import com.socially.user.findbyfederatedidentity.application.input.FindUserByFederatedIdentityQuery;
import com.socially.user.findbyfederatedidentity.application.port.left.FindUserByFederatedIdentityUseCase;
import com.socially.user.findbyfederatedidentity.domain.port.right.FindUserByFederatedIdentityRepository;
import com.socially.user.kernel.domain.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public final class FindUserByFederatedIdentityQueryHandler
    implements FindUserByFederatedIdentityUseCase {

  private final FindUserByFederatedIdentityRepository findUserByFederatedIdentityRepository;

  @Override
  public Optional<User> execute(FindUserByFederatedIdentityQuery query) {
    requireText(query.issuer(), "issuer");
    requireText(query.subject(), "subject");
    return findUserByFederatedIdentityRepository.findByIssuerAndSubject(
        query.issuer().trim(), query.subject().trim());
  }

  private static void requireText(String value, String fieldName) {
    if (!StringUtils.hasText(value)) {
      throw new IllegalArgumentException(fieldName + " cannot be blank");
    }
  }
}
