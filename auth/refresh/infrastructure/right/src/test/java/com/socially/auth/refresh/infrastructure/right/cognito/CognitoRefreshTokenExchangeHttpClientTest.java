package com.socially.auth.refresh.infrastructure.right.cognito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.refresh.application.exception.RefreshTokenExchangeFailedException;
import com.socially.auth.refresh.application.exception.RefreshTokenRejectedException;
import com.socially.auth.refresh.infrastructure.right.cognito.mapper.RefreshTokenExchangeFormMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class CognitoRefreshTokenExchangeHttpClientTest {

  @Mock private AuthProperties authProperties;
  @Mock private RefreshTokenExchangeFormMapper formMapper;

  private RestClient restClient;
  private MockRestServiceServer server;
  private CognitoRefreshTokenExchangeHttpClient sut;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    restClient = builder.build();
    sut = new CognitoRefreshTokenExchangeHttpClient(authProperties, formMapper, restClient);
  }

  @AfterEach
  void tearDown() {
    server.verify();
  }

  @Test
  void exchangeRefreshToken_should_perform_post_request() {
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1")).thenReturn(minimalForm());

    server
        .expect(requestTo("https://hosted.example/oauth2/token"))
        .andExpect(method(POST))
        .andRespond(
            withSuccess(
                "{\"access_token\":\"a\",\"id_token\":\"i\",\"refresh_token\":\"r\",\"token_type\":\"Bearer\",\"expires_in\":3600}",
                MediaType.APPLICATION_JSON));

    sut.exchangeRefreshToken("refresh-1");
  }

  static Stream<Arguments> oauthBaseUrlCases() {
    return Stream.of(
        Arguments.of(
            "https://oauth.custom", "https://hosted.fallback", "https://oauth.custom/oauth2/token"),
        Arguments.of(null, "https://hosted.fallback", "https://hosted.fallback/oauth2/token"),
        Arguments.of("", "https://hosted.fallback", "https://hosted.fallback/oauth2/token"));
  }

  @ParameterizedTest
  @MethodSource("oauthBaseUrlCases")
  void exchangeRefreshToken_should_perform_request_with_correct_oauth_base_url(
      String oauthBaseUrl, String hostedDomain, String expectedTokenUri) {
    if (oauthBaseUrl != null && !oauthBaseUrl.isEmpty()) {
      when(authProperties.oauthApiBaseUrl()).thenReturn(oauthBaseUrl);
    } else {
      if (oauthBaseUrl != null) {
        lenient().when(authProperties.oauthApiBaseUrl()).thenReturn("");
      }
      lenient().when(authProperties.oauthHostedDomain()).thenReturn(hostedDomain);
    }
    when(formMapper.toForm(anyString())).thenReturn(minimalForm());

    server
        .expect(requestTo(expectedTokenUri))
        .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

    sut.exchangeRefreshToken("refresh-1");
  }

  @Test
  void exchangeRefreshToken_should_perform_request_with_application_form_content_type_header() {
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1")).thenReturn(minimalForm());

    server
        .expect(requestTo("https://hosted.example/oauth2/token"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
        .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

    sut.exchangeRefreshToken("refresh-1");
  }

  @Test
  void exchangeRefreshToken_should_call_refresh_token_exchange_form_mapper_with_refresh_token() {
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1")).thenReturn(minimalForm());

    server
        .expect(requestTo("https://hosted.example/oauth2/token"))
        .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

    sut.exchangeRefreshToken("refresh-1");

    verify(formMapper).toForm("refresh-1");
  }

  @Test
  void exchangeRefreshToken_should_perform_request_with_body_built_by_mapper() {
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");

    MultiValueMap<String, String> expectedForm = new LinkedMultiValueMap<>();
    expectedForm.add("grant_type", "refresh_token");
    expectedForm.add("client_id", "spa");
    expectedForm.add("refresh_token", "refresh-1");
    when(formMapper.toForm("refresh-1")).thenReturn(expectedForm);

    server
        .expect(requestTo("https://hosted.example/oauth2/token"))
        .andExpect(content().formData(expectedForm))
        .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

    sut.exchangeRefreshToken("refresh-1");
  }

  @Test
  void exchangeRefreshToken_should_return_cognito_token_response() {
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1")).thenReturn(minimalForm());

    server
        .expect(requestTo("https://hosted.example/oauth2/token"))
        .andRespond(
            withSuccess(
                "{\"access_token\":\"access-1\",\"id_token\":\"id-1\",\"refresh_token\":\"refresh-1\",\"token_type\":\"Bearer\",\"expires_in\":7200}",
                MediaType.APPLICATION_JSON));

    OAuthTokenResponse response = sut.exchangeRefreshToken("refresh-1");

    assertEquals("access-1", response.accessToken());
    assertEquals("id-1", response.idToken());
    assertEquals("refresh-1", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(7200L, response.expiresIn());
  }

  @Test
  void exchangeRefreshToken_should_throw_exception_when_request_fails() {
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1")).thenReturn(minimalForm());

    server.expect(requestTo("https://hosted.example/oauth2/token")).andRespond(withServerError());

    RefreshTokenExchangeFailedException exception =
        assertThrows(
            RefreshTokenExchangeFailedException.class, () -> sut.exchangeRefreshToken("refresh-1"));

    assertEquals("Failed to refresh OAuth tokens", exception.getMessage());
  }

  @Test
  void exchangeRefreshToken_should_throw_when_oauth_returns_bad_request() {
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1")).thenReturn(minimalForm());

    server.expect(requestTo("https://hosted.example/oauth2/token")).andRespond(withBadRequest());

    RefreshTokenRejectedException exception =
        assertThrows(
            RefreshTokenRejectedException.class, () -> sut.exchangeRefreshToken("refresh-1"));

    assertEquals("Refresh token rejected", exception.getMessage());
  }

  private static MultiValueMap<String, String> minimalForm() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "refresh_token");
    form.add("client_id", "x");
    form.add("refresh_token", "x");
    return form;
  }
}
