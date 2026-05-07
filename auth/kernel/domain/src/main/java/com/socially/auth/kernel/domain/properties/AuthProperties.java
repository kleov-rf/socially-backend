package com.socially.auth.kernel.domain.properties;

import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(OAuthProperties oauth, String redirectUri, CookiesProperties cookies) {

  public Boolean cookiesSecure() {
    if (cookies == null) {
      return true;
    }
    return cookies.secure();
  }

  public String refreshCookieName() {
    if (cookies == null || cookies.names() == null) {
      return null;
    }
    return cookies.names().refresh();
  }

  public String stateCookieName() {
    if (cookies == null || cookies.names() == null) {
      return null;
    }
    return cookies.names().state();
  }

  public String pkceCookieName() {
    if (cookies == null || cookies.names() == null) {
      return null;
    }
    return cookies.names().pkce();
  }

  public String oauthHostedDomain() {
    if (oauth == null) {
      return null;
    }
    return oauth.hostedDomain();
  }

  public String oauthApiBaseUrl() {
    if (oauth == null) {
      return null;
    }
    return oauth.baseUrl();
  }

  public String oauthIdentityProvider() {
    if (oauth == null) {
      return null;
    }
    return oauth.identityProvider();
  }

  public String oauthSpaClientId() {
    if (oauth == null || oauth.credentials() == null || oauth.credentials().spa() == null) {
      return null;
    }
    return oauth.credentials().spa().clientId();
  }

  public Optional<CredentialsProperties.Backend> oauthBackendCredentials() {
    if (oauth == null) {
      return Optional.empty();
    }
    CredentialsProperties credentials = oauth.credentials();
    if (credentials == null || credentials.backend() == null) {
      return Optional.empty();
    }
    return Optional.of(credentials.backend());
  }
}
