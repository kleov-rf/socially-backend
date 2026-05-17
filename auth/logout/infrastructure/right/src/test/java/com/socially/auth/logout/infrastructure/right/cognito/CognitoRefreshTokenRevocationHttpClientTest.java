package com.socially.auth.logout.infrastructure.right.cognito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.ExpectedCount.never;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socially.auth.kernel.domain.exception.AuthInternalErrorException;
import com.socially.auth.kernel.domain.properties.AuthProperties;
import com.socially.auth.kernel.domain.properties.CredentialsProperties;
import com.socially.auth.logout.infrastructure.right.cognito.mapper.RefreshTokenRevocationFormMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class CognitoRefreshTokenRevocationHttpClientTest {

  private static final String BACKEND_CLIENT_ID = "backend-client-id";
  private static final String BACKEND_CLIENT_SECRET = "backend-client-secret";
  private static final String BACKEND_SECRET_JSON =
      "{\"client_secret\":\"" + BACKEND_CLIENT_SECRET + "\"}";

  @Mock private AuthProperties authProperties;
  @Mock private ObjectMapper objectMapper;
  @Mock private RefreshTokenRevocationFormMapper formMapper;

  private RestClient restClient;
  private MockRestServiceServer server;
  private CognitoRefreshTokenRevocationHttpClient sut;

  @BeforeEach
  void setUp() {
    RestClient.Builder builder = RestClient.builder();
    server = MockRestServiceServer.bindTo(builder).build();
    restClient = builder.build();
    sut =
        new CognitoRefreshTokenRevocationHttpClient(
            authProperties, objectMapper, formMapper, restClient);
  }

  @AfterEach
  void tearDown() {
    server.verify();
  }

  @Test
  void revokeRefreshToken_should_not_perform_request_when_token_is_missing() {
    server.expect(never(), anything());

    sut.revokeRefreshToken("");
  }

  @Test
  void revokeRefreshToken_should_not_perform_request_when_backend_credentials_are_missing() {
    when(authProperties.oauthBackendCredentials()).thenReturn(Optional.empty());

    server.expect(never(), anything());

    sut.revokeRefreshToken("refresh-1");
  }

  @Test
  void revokeRefreshToken_should_not_perform_request_when_backend_client_id_is_missing()
      throws Exception {
    mockBackendCredentials("", BACKEND_SECRET_JSON, BACKEND_CLIENT_SECRET);

    server.expect(never(), anything());

    sut.revokeRefreshToken("refresh-1");
  }

  @Test
  void revokeRefreshToken_should_not_perform_request_when_backend_client_secret_is_missing()
      throws Exception {
    mockBackendCredentials(BACKEND_CLIENT_ID, "{\"client_secret\":\"\"}", "");

    server.expect(never(), anything());

    sut.revokeRefreshToken("refresh-1");
  }

  @Test
  void
      revokeRefreshToken_should_call_refresh_token_revocation_form_mapper_with_token_and_client_id()
          throws Exception {
    mockBackendCredentials(BACKEND_CLIENT_ID, BACKEND_SECRET_JSON, BACKEND_CLIENT_SECRET);
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1", BACKEND_CLIENT_ID)).thenReturn(minimalForm());

    server.expect(requestTo("https://hosted.example/oauth2/revoke")).andRespond(withSuccess());

    sut.revokeRefreshToken("refresh-1");

    verify(formMapper).toForm("refresh-1", BACKEND_CLIENT_ID);
  }

  @Test
  void revokeRefreshToken_should_perform_post_request() throws Exception {
    mockBackendCredentials(BACKEND_CLIENT_ID, BACKEND_SECRET_JSON, BACKEND_CLIENT_SECRET);
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1", BACKEND_CLIENT_ID)).thenReturn(minimalForm());

    server
        .expect(requestTo("https://hosted.example/oauth2/revoke"))
        .andExpect(method(POST))
        .andRespond(withSuccess());

    sut.revokeRefreshToken("refresh-1");
  }

  static Stream<Arguments> oauthBaseUrlCases() {
    return Stream.of(
        Arguments.of(
            "https://oauth.custom",
            "https://hosted.fallback",
            "https://oauth.custom/oauth2/revoke"),
        Arguments.of(null, "https://hosted.fallback", "https://hosted.fallback/oauth2/revoke"),
        Arguments.of("", "https://hosted.fallback", "https://hosted.fallback/oauth2/revoke"));
  }

  @ParameterizedTest
  @MethodSource("oauthBaseUrlCases")
  void revokeRefreshToken_should_perform_request_with_correct_oauth_base_url(
      String oauthBaseUrl, String hostedDomain, String expectedRevokeUri) throws Exception {
    mockBackendCredentials(BACKEND_CLIENT_ID, BACKEND_SECRET_JSON, BACKEND_CLIENT_SECRET);
    if (oauthBaseUrl != null && !oauthBaseUrl.isEmpty()) {
      when(authProperties.oauthApiBaseUrl()).thenReturn(oauthBaseUrl);
    } else {
      if (oauthBaseUrl != null) {
        lenient().when(authProperties.oauthApiBaseUrl()).thenReturn("");
      }
      lenient().when(authProperties.oauthHostedDomain()).thenReturn(hostedDomain);
    }
    when(formMapper.toForm(anyString(), anyString())).thenReturn(minimalForm());

    server.expect(requestTo(expectedRevokeUri)).andRespond(withSuccess());

    sut.revokeRefreshToken("refresh-1");
  }

  @Test
  void revokeRefreshToken_should_perform_request_with_application_form_content_type_header()
      throws Exception {
    mockBackendCredentials(BACKEND_CLIENT_ID, BACKEND_SECRET_JSON, BACKEND_CLIENT_SECRET);
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1", BACKEND_CLIENT_ID)).thenReturn(minimalForm());

    server
        .expect(requestTo("https://hosted.example/oauth2/revoke"))
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
        .andRespond(withSuccess());

    sut.revokeRefreshToken("refresh-1");
  }

  @Test
  void revokeRefreshToken_should_perform_request_with_basic_auth_header() throws Exception {
    mockBackendCredentials(BACKEND_CLIENT_ID, BACKEND_SECRET_JSON, BACKEND_CLIENT_SECRET);
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1", BACKEND_CLIENT_ID)).thenReturn(minimalForm());

    String expectedAuth =
        "Basic "
            + Base64.getEncoder()
                .encodeToString(
                    (BACKEND_CLIENT_ID + ":" + BACKEND_CLIENT_SECRET)
                        .getBytes(StandardCharsets.UTF_8));

    server
        .expect(requestTo("https://hosted.example/oauth2/revoke"))
        .andExpect(header(HttpHeaders.AUTHORIZATION, expectedAuth))
        .andRespond(withSuccess());

    sut.revokeRefreshToken("refresh-1");
  }

  @Test
  void revokeRefreshToken_should_perform_request_with_body_built_by_mapper() throws Exception {
    mockBackendCredentials(BACKEND_CLIENT_ID, BACKEND_SECRET_JSON, BACKEND_CLIENT_SECRET);
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");

    MultiValueMap<String, String> expectedForm = new LinkedMultiValueMap<>();
    expectedForm.add("token", "refresh-1");
    expectedForm.add("client_id", BACKEND_CLIENT_ID);
    when(formMapper.toForm("refresh-1", BACKEND_CLIENT_ID)).thenReturn(expectedForm);

    server
        .expect(requestTo("https://hosted.example/oauth2/revoke"))
        .andExpect(content().formData(expectedForm))
        .andRespond(withSuccess());

    sut.revokeRefreshToken("refresh-1");
  }

  @Test
  void revokeRefreshToken_should_not_throw_when_request_fails() throws Exception {
    mockBackendCredentials(BACKEND_CLIENT_ID, BACKEND_SECRET_JSON, BACKEND_CLIENT_SECRET);
    lenient().when(authProperties.oauthHostedDomain()).thenReturn("https://hosted.example");
    when(formMapper.toForm("refresh-1", BACKEND_CLIENT_ID)).thenReturn(minimalForm());

    server.expect(requestTo("https://hosted.example/oauth2/revoke")).andRespond(withServerError());

    assertDoesNotThrow(() -> sut.revokeRefreshToken("refresh-1"));
  }

  @Test
  void revokeRefreshToken_should_throw_internal_server_error_when_backend_secret_json_is_invalid()
      throws Exception {
    when(authProperties.oauthBackendCredentials())
        .thenReturn(
            Optional.of(new CredentialsProperties.Backend(BACKEND_CLIENT_ID, "{invalid-json")));
    doThrow(new RuntimeException("invalid json")).when(objectMapper).readTree(anyString());

    AuthInternalErrorException exception =
        assertThrows(AuthInternalErrorException.class, () -> sut.revokeRefreshToken("refresh-1"));

    assertEquals("Invalid backend OAuth client secret JSON configuration", exception.getMessage());
  }

  private void mockBackendCredentials(
      String backendClientId, String secretJson, String clientSecret) throws Exception {
    when(authProperties.oauthBackendCredentials())
        .thenReturn(Optional.of(new CredentialsProperties.Backend(backendClientId, secretJson)));

    JsonNode root = mock(JsonNode.class);
    JsonNode secretNode = mock(JsonNode.class);
    doReturn(root).when(objectMapper).readTree(secretJson);
    when(root.get("client_secret")).thenReturn(secretNode);
    when(secretNode.asText()).thenReturn(clientSecret);
  }

  private static MultiValueMap<String, String> minimalForm() {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("token", "refresh-1");
    form.add("client_id", BACKEND_CLIENT_ID);
    return form;
  }
}
