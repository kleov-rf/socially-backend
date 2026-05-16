package com.socially.auth.kernel.infrastructure.right.adapter.user;

import com.socially.auth.kernel.domain.AuthUser;
import com.socially.auth.kernel.domain.exception.MissingOidcIdentityClaimsException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public final class AuthUserClaimsValidator {

  public void requireIssuerAndSubject(AuthUser authUser) {
    if (!StringUtils.hasText(authUser.issuer()) || !StringUtils.hasText(authUser.subject())) {
      throw new MissingOidcIdentityClaimsException();
    }
  }
}
