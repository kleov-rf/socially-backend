package com.socially.auth.login.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.socially.auth.kernel.domain.properties.AuthProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class LoginAuthorizeUrlMapperTest {

  @Mock private AuthProperties authProperties;

  @InjectMocks private LoginAuthorizeUrlMapper sut;

  @Test
  void toAuthorizeUrl_should_return_url_with_hosted_domain() {
    stubBaseProperties();

    String url = sut.toAuthorizeUrl("state-n", "challenge-n");

    assertTrue(url.contains("/oauth2/authorize"));
    assertTrue(url.startsWith("https://auth.example.amazoncognito.com"));
  }

  @Test
  void toAuthorizeUrl_should_return_url_with_response_type_param() {
    stubBaseProperties();

    String url = sut.toAuthorizeUrl("state-n", "challenge-n");

    assertEquals(
        "code",
        UriComponentsBuilder.fromUriString(url).build().getQueryParams().getFirst("response_type"));
  }

  @Test
  void toAuthorizeUrl_should_return_url_with_scope_param() {
    stubBaseProperties();

    String url = sut.toAuthorizeUrl("state-n", "challenge-n");

    String scope =
        UriComponentsBuilder.fromUriString(url).build().getQueryParams().getFirst("scope");
    assertNotNull(scope);
    assertTrue(scope.contains("openid") && scope.contains("email") && scope.contains("profile"));
  }

  @Test
  void toAuthorizeUrl_should_return_url_with_code_challenge_method_param() {
    stubBaseProperties();

    String url = sut.toAuthorizeUrl("state-n", "challenge-n");

    assertEquals(
        "S256",
        UriComponentsBuilder.fromUriString(url)
            .build()
            .getQueryParams()
            .getFirst("code_challenge_method"));
  }

  @Test
  void toAuthorizeUrl_should_return_url_with_state_param() {
    stubBaseProperties();

    String url = sut.toAuthorizeUrl("my-state", "challenge-n");

    assertEquals(
        "my-state",
        UriComponentsBuilder.fromUriString(url).build().getQueryParams().getFirst("state"));
  }

  @Test
  void toAuthorizeUrl_should_return_url_with_code_challenge_param() {
    stubBaseProperties();

    String url = sut.toAuthorizeUrl("state-n", "my-challenge");

    assertEquals(
        "my-challenge",
        UriComponentsBuilder.fromUriString(url)
            .build()
            .getQueryParams()
            .getFirst("code_challenge"));
  }

  @Test
  void toAuthorizeUrl_should_return_url_with_client_id_param() {
    stubBaseProperties();

    String url = sut.toAuthorizeUrl("state-n", "challenge-n");

    assertEquals(
        "spa-client-id",
        UriComponentsBuilder.fromUriString(url).build().getQueryParams().getFirst("client_id"));
  }

  @Test
  void toAuthorizeUrl_should_return_url_with_redirect_uri_param() {
    stubBaseProperties();

    String url = sut.toAuthorizeUrl("state-n", "challenge-n");

    assertTrue(url.contains("redirect_uri="));
    assertTrue(url.contains("localhost"));
    assertTrue(url.contains("5173"));
    assertTrue(url.contains("callback"));
    assertTrue(url.contains("google"));
  }

  @Test
  void toAuthorizeUrl_should_return_url_with_identity_provider_param() {
    stubBaseProperties();
    when(authProperties.oauthIdentityProvider()).thenReturn("Google");

    String url = sut.toAuthorizeUrl("state-n", "challenge-n");

    assertEquals(
        "Google",
        UriComponentsBuilder.fromUriString(url)
            .build()
            .getQueryParams()
            .getFirst("identity_provider"));
  }

  @Test
  void
      toAuthorizeUrl_should_not_return_url_with_identity_provider_param_when_identity_provider_not_present() {
    stubBaseProperties();
    when(authProperties.oauthIdentityProvider()).thenReturn("");

    String url = sut.toAuthorizeUrl("state-n", "challenge-n");

    assertFalse(url.contains("identity_provider="));
  }

  private void stubBaseProperties() {
    when(authProperties.oauthHostedDomain()).thenReturn("https://auth.example.amazoncognito.com");
    when(authProperties.oauthSpaClientId()).thenReturn("spa-client-id");
    when(authProperties.redirectUri()).thenReturn("http://localhost:5173/auth/callback/google");
  }
}
