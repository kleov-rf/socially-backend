package com.socially.auth.kernel.domain.properties;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AuthPropertiesTest {

  @Test
  void cookiesSecure_should_return_true_when_cookies_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    Boolean actual = authProperties.cookiesSecure();

    assertTrue(actual);
  }

  @ParameterizedTest
  @CsvSource({"true, true", "false, false"})
  void cookiesSecure_should_return_cookies_secure(Boolean cookiesSecure, Boolean expected) {
    CookiesProperties cookies = new CookiesProperties(cookiesSecure, null);
    AuthProperties authProperties = new AuthProperties(null, null, cookies);

    Boolean actual = authProperties.cookiesSecure();

    assertEquals(expected, actual);
  }

  @Test
  void refreshCookieName_should_return_null_when_cookies_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    String actual = authProperties.refreshCookieName();

    assertNull(actual);
  }

  @Test
  void refreshCookieName_should_return_null_when_cookies_names_is_null() {
    CookiesProperties cookies = new CookiesProperties(true, null);
    AuthProperties authProperties = new AuthProperties(null, null, cookies);

    String actual = authProperties.refreshCookieName();

    assertNull(actual);
  }

  @Test
  void refreshCookieName_should_return_refresh_cookie_name() {
    String expected = "refresh_token";
    CookiesProperties.CookieNamesProperties names =
        new CookiesProperties.CookieNamesProperties(expected, "state_token", "pkce_token");
    CookiesProperties cookies = new CookiesProperties(true, names);
    AuthProperties authProperties = new AuthProperties(null, null, cookies);

    String actual = authProperties.refreshCookieName();

    assertEquals(expected, actual);
  }

  @Test
  void stateCookieName_should_return_null_when_cookies_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    String actual = authProperties.stateCookieName();

    assertNull(actual);
  }

  @Test
  void stateCookieName_should_return_null_when_cookies_names_is_null() {
    CookiesProperties cookies = new CookiesProperties(true, null);
    AuthProperties authProperties = new AuthProperties(null, null, cookies);

    String actual = authProperties.stateCookieName();

    assertNull(actual);
  }

  @Test
  void stateCookieName_should_return_state_cookie_name() {
    String expected = "state_token";
    CookiesProperties.CookieNamesProperties names =
        new CookiesProperties.CookieNamesProperties("refresh_token", expected, "pkce_token");
    CookiesProperties cookies = new CookiesProperties(true, names);
    AuthProperties authProperties = new AuthProperties(null, null, cookies);

    String actual = authProperties.stateCookieName();

    assertEquals(expected, actual);
  }

  @Test
  void pkceCookieName_should_return_null_when_cookies_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    String actual = authProperties.pkceCookieName();

    assertNull(actual);
  }

  @Test
  void pkceCookieName_should_return_null_when_cookies_names_is_null() {
    CookiesProperties cookies = new CookiesProperties(true, null);
    AuthProperties authProperties = new AuthProperties(null, null, cookies);

    String actual = authProperties.pkceCookieName();

    assertNull(actual);
  }

  @Test
  void pkceCookieName_should_return_pkce_cookie_name() {
    String expected = "pkce_token";
    CookiesProperties.CookieNamesProperties names =
        new CookiesProperties.CookieNamesProperties("refresh_token", "state_token", expected);
    CookiesProperties cookies = new CookiesProperties(true, names);
    AuthProperties authProperties = new AuthProperties(null, null, cookies);

    String actual = authProperties.pkceCookieName();

    assertEquals(expected, actual);
  }

  @Test
  void oauthHostedDomain_should_return_null_when_oauth_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    String actual = authProperties.oauthHostedDomain();

    assertNull(actual);
  }

  @Test
  void oauthHostedDomain_should_return_hosted_domain_when_oauth_is_not_null() {
    String expected = "hosted.domain.com";
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1", "pool-id", expected, "https://oauth.base", "provider", false, null);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    String actual = authProperties.oauthHostedDomain();

    assertEquals(expected, actual);
  }

  @Test
  void oauthApiBaseUrl_should_return_null_when_oauth_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    String actual = authProperties.oauthApiBaseUrl();

    assertNull(actual);
  }

  @Test
  void oauthApiBaseUrl_should_return_oauth_base_url_when_oauth_is_not_null() {
    String expected = "https://oauth.base";
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1", "pool-id", "hosted.domain.com", expected, "provider", false, null);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    String actual = authProperties.oauthApiBaseUrl();

    assertEquals(expected, actual);
  }

  @Test
  void oauthIdentityProvider_should_return_null_when_oauth_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    String actual = authProperties.oauthIdentityProvider();

    assertNull(actual);
  }

  @Test
  void oauthIdentityProvider_should_return_identity_provider_when_oauth_is_not_null() {
    String expected = "my-provider";
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1",
            "pool-id",
            "hosted.domain.com",
            "https://oauth.base",
            expected,
            false,
            null);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    String actual = authProperties.oauthIdentityProvider();

    assertEquals(expected, actual);
  }

  @Test
  void oauthSpaClientId_should_return_null_when_oauth_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    String actual = authProperties.oauthSpaClientId();

    assertNull(actual);
  }

  @Test
  void oauthSpaClientId_should_return_null_when_credentials_is_null() {
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1",
            "pool-id",
            "hosted.domain.com",
            "https://oauth.base",
            "provider",
            false,
            null);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    String actual = authProperties.oauthSpaClientId();

    assertNull(actual);
  }

  @Test
  void oauthSpaClientId_should_return_null_when_spa_is_null() {
    CredentialsProperties credentials = new CredentialsProperties(null, null);
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1",
            "pool-id",
            "hosted.domain.com",
            "https://oauth.base",
            "provider",
            false,
            credentials);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    String actual = authProperties.oauthSpaClientId();

    assertNull(actual);
  }

  @Test
  void oauthSpaClientId_should_return_spa_client_id() {
    String expected = "spa-client-id";
    CredentialsProperties.Spa spa = new CredentialsProperties.Spa(expected);
    CredentialsProperties credentials = new CredentialsProperties(spa, null);
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1",
            "pool-id",
            "hosted.domain.com",
            "https://oauth.base",
            "provider",
            false,
            credentials);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    String actual = authProperties.oauthSpaClientId();

    assertEquals(expected, actual);
  }

  @Test
  void oauthBackendCredentials_should_return_empty_when_oauth_is_null() {
    AuthProperties authProperties = new AuthProperties(null, null, null);

    Optional<CredentialsProperties.Backend> actual = authProperties.oauthBackendCredentials();

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void oauthBackendCredentials_should_return_empty_when_credentials_is_null() {
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1",
            "pool-id",
            "hosted.domain.com",
            "https://oauth.base",
            "provider",
            false,
            null);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    Optional<CredentialsProperties.Backend> actual = authProperties.oauthBackendCredentials();

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void oauthBackendCredentials_should_return_empty_when_backend_is_null() {
    CredentialsProperties credentials = new CredentialsProperties(null, null);
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1",
            "pool-id",
            "hosted.domain.com",
            "https://oauth.base",
            "provider",
            false,
            credentials);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    Optional<CredentialsProperties.Backend> actual = authProperties.oauthBackendCredentials();

    assertEquals(Optional.empty(), actual);
  }

  @Test
  void oauthBackendCredentials_should_return_backend_credentials() {
    CredentialsProperties.Backend expected =
        new CredentialsProperties.Backend("backend-client-id", "secret-json");
    CredentialsProperties credentials = new CredentialsProperties(null, expected);
    OAuthProperties oauth =
        new OAuthProperties(
            "us-east-1",
            "pool-id",
            "hosted.domain.com",
            "https://oauth.base",
            "provider",
            false,
            credentials);
    AuthProperties authProperties = new AuthProperties(oauth, null, null);

    Optional<CredentialsProperties.Backend> actual = authProperties.oauthBackendCredentials();

    assertTrue(actual.isPresent());
    assertEquals(expected, actual.get());
  }
}
