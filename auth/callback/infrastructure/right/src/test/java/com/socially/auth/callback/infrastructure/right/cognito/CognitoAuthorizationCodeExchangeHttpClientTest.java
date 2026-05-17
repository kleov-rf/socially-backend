package com.socially.auth.callback.infrastructure.right.cognito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.socially.auth.callback.infrastructure.right.cognito.mapper.AuthorizationCodeExchangeFormMapper;
import com.socially.auth.kernel.domain.OAuthTokenResponse;
import com.socially.auth.kernel.domain.exception.AuthUpstreamFailureException;
import com.socially.auth.kernel.domain.properties.AuthProperties;
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
class CognitoAuthorizationCodeExchangeHttpClientTest {

  @Mock private AuthProperties authProperties;
  @Mock private AuthorizationCodeExchangeFormMapper formMapper;

  private RestClient restClient;
  private MockRestServiceServer server;
  private CognitoAuthorizationCodeExchangeHttpClient sut;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    restClient = builder.build();
    sut = new CognitoAuthorizationCodeExchangeHttpClient(authProperties, formMapper, restClient);
  }

  @AfterEach
  void tearDown() {
    server.verify();
  }

  @Test
  void exchangeAuthorizationCode_should_perform_post_request() {
    when(authProperties.oauthApiBaseUrl()).thenReturn("https://oauth.example");
    MultiValueMap<String, String> form = minimalForm();
    when(formMapper.toForm("code-1", "verifier-1")).thenReturn(form);

    server
        .expect(requestTo("https://oauth.example/oauth2/token"))
        .andExpect(method(POST))
        .andRespond(
            withSuccess(
                "{\"access_token\":\"a\",\"id_token\":\"i\",\"refresh_token\":\"r\",\"token_type\":\"Bearer\",\"expires_in\":3600}",
                MediaType.APPLICATION_JSON));

    sut.exchangeAuthorizationCode("code-1", "verifier-1");
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
  void exchangeAuthorizationCode_should_perform_request_with_correct_oauth_base_url(
      String oauthBaseUrl, String hostedDomain, String expectedTokenUri) {
    if (oauthBaseUrl != null && !oauthBaseUrl.isEmpty()) {
      when(authProperties.oauthApiBaseUrl()).thenReturn(oauthBaseUrl);
    } else {
      if (oauthBaseUrl != null) {
        when(authProperties.oauthApiBaseUrl()).thenReturn("");
      }
      when(authProperties.oauthHostedDomain()).thenReturn(hostedDomain);
    }
    when(formMapper.toForm(anyString(), anyString())).thenReturn(minimalForm());

    server
        .expect(requestTo(expectedTokenUri))
        .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

    sut.exchangeAuthorizationCode("c", "v");
  }

  @Test
  void
      exchangeAuthorizationCode_should_perform_request_with_application_form_content_type_header() {
    when(authProperties.oauthApiBaseUrl()).thenReturn("https://oauth.example");
    when(formMapper.toForm("c", "v")).thenReturn(minimalForm());

    server
        .expect(requestTo("https://oauth.example/oauth2/token"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
        .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

    sut.exchangeAuthorizationCode("c", "v");
  }

  @Test
  void
      exchangeAuthorizationCode_should_call_authorization_code_exchange_form_mapper_with_code_and_code_verifier() {
    when(authProperties.oauthApiBaseUrl()).thenReturn("https://oauth.example");
    when(formMapper.toForm("code-1", "verifier-1")).thenReturn(minimalForm());

    server
        .expect(requestTo("https://oauth.example/oauth2/token"))
        .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

    sut.exchangeAuthorizationCode("code-1", "verifier-1");

    verify(formMapper).toForm("code-1", "verifier-1");
  }

  @Test
  void exchangeAuthorizationCode_should_perform_request_with_body_built_by_mapper() {
    when(authProperties.oauthApiBaseUrl()).thenReturn("https://oauth.example");

    MultiValueMap<String, String> expectedForm = new LinkedMultiValueMap<>();
    expectedForm.add("grant_type", "authorization_code");
    expectedForm.add("client_id", "spa");
    expectedForm.add("code", "code-1");
    expectedForm.add("redirect_uri", "http://localhost/cb");
    expectedForm.add("code_verifier", "verifier-1");
    when(formMapper.toForm("code-1", "verifier-1")).thenReturn(expectedForm);

    server
        .expect(requestTo("https://oauth.example/oauth2/token"))
        .andExpect(content().formData(expectedForm))
        .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

    sut.exchangeAuthorizationCode("code-1", "verifier-1");
  }

  @Test
  void exchangeAuthorizationCode_should_return_cognito_token_response() {
    when(authProperties.oauthApiBaseUrl()).thenReturn("https://oauth.example");
    when(formMapper.toForm("c", "v")).thenReturn(minimalForm());

    server
        .expect(requestTo("https://oauth.example/oauth2/token"))
        .andRespond(
            withSuccess(
                "{\"access_token\":\"access-1\",\"id_token\":\"id-1\",\"refresh_token\":\"refresh-1\",\"token_type\":\"Bearer\",\"expires_in\":7200}",
                MediaType.APPLICATION_JSON));

    OAuthTokenResponse response = sut.exchangeAuthorizationCode("c", "v");

    assertEquals("access-1", response.accessToken());
    assertEquals("id-1", response.idToken());
    assertEquals("refresh-1", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(7200L, response.expiresIn());
  }

  @Test
  void exchangeAuthorizationCode_should_throw_exception_when_request_fails() {
    when(authProperties.oauthApiBaseUrl()).thenReturn("https://oauth.example");
    when(formMapper.toForm("c", "v")).thenReturn(minimalForm());

    server.expect(requestTo("https://oauth.example/oauth2/token")).andRespond(withServerError());

    AuthUpstreamFailureException exception =
        assertThrows(
            AuthUpstreamFailureException.class, () -> sut.exchangeAuthorizationCode("c", "v"));

    assertEquals("Failed to exchange OAuth authorization code", exception.getMessage());
  }

  private static MultiValueMap<String, String> minimalForm() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", "x");
    form.add("code", "x");
    form.add("redirect_uri", "x");
    form.add("code_verifier", "x");
    return form;
  }
}
