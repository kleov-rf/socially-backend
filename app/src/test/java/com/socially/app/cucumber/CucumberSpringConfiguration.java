package com.socially.app.cucumber;

import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.cucumber.spring.CucumberContextConfiguration;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(CucumberSpringConfiguration.TestConfig.class)
public class CucumberSpringConfiguration {

  public static final String MEDIA_BUCKET = "socially-media";
  static final String MEDIA_REGION = "us-east-1";

  static final HttpServer oauthServer = createOauthServer();

  static final PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:15-alpine")
          .withDatabaseName("socially")
          .withUsername("postgres")
          .withPassword("postgres");

  static final LocalStackContainer localstack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
          .withServices("s3");

  static {
    postgres.start();
    localstack.start();
    oauthServer.start();
    createMediaBucket();
    Runtime.getRuntime().addShutdownHook(new Thread(postgres::stop));
    Runtime.getRuntime().addShutdownHook(new Thread(localstack::stop));
    Runtime.getRuntime().addShutdownHook(new Thread(() -> oauthServer.stop(0)));
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add(
        "spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> CucumberOAuthJwt.TEST_ISSUER);
    registry.add("auth.oauth.use-ministack", () -> "false");
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add(
        "auth.oauth.hosted-domain", () -> "http://localhost:" + oauthServer.getAddress().getPort());
    registry.add("auth.oauth.identity-provider", () -> "Google");
    registry.add("auth.oauth.credentials.spa.client-id", () -> "test-spa-client");
    registry.add("auth.oauth.credentials.backend.client-id", () -> "test-backend-client");
    registry.add(
        "auth.oauth.credentials.backend.client-secret-json",
        () -> "{\"client_secret\":\"test-backend-secret\"}");
    registry.add("auth.redirect-uri", () -> "http://localhost:5173/auth/callback/google");
    registry.add("auth.cookies.secure", () -> "false");

    registry.add("media.storage.s3.bucket", () -> MEDIA_BUCKET);
    registry.add("media.storage.s3.region", () -> MEDIA_REGION);
    registry.add("media.storage.s3.endpoint-url", () -> localstack.getEndpoint().toString());
    registry.add(
        "media.storage.cdn.base-url",
        () -> localstack.getEndpoint().toString() + "/" + MEDIA_BUCKET);
    registry.add("media.storage.presign.duration", () -> "PT15M");
  }

  private static void createMediaBucket() {
    try (S3Client s3Client = createS3Client()) {
      s3Client.createBucket(CreateBucketRequest.builder().bucket(MEDIA_BUCKET).build());
    }
  }

  public static S3Client createTestS3Client() {
    return createS3Client();
  }

  private static S3Client createS3Client() {
    return S3Client.builder()
        .endpointOverride(localstack.getEndpoint())
        .region(Region.of(localstack.getRegion()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
        .forcePathStyle(true)
        .build();
  }

  private static HttpServer createOauthServer() {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
      server.createContext("/oauth2/token", CucumberSpringConfiguration::handleTokenExchange);
      server.createContext("/oauth2/revoke", CucumberSpringConfiguration::handleRevoke);
      return server;
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to create OAuth test server", exception);
    }
  }

  private static void handleTokenExchange(HttpExchange exchange) throws IOException {
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      exchange.sendResponseHeaders(405, -1);
      return;
    }

    String idTokenPayload =
        Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(
                "{\"iss\":\"https://test-idp.example\",\"sub\":\"auth-user-1\",\"email\":\"auth.user@example.com\",\"given_name\":\"Auth\",\"family_name\":\"User\"}"
                    .getBytes(StandardCharsets.UTF_8));
    String idToken = "header." + idTokenPayload + ".signature";

    String body =
        """
        {
          "access_token": "phase1-access-token",
          "id_token": "%s",
          "refresh_token": "phase1-refresh-token",
          "token_type": "Bearer",
          "expires_in": 3600
        }
        """
            .formatted(idToken);
    writeJson(exchange, 200, body);
  }

  private static void handleRevoke(HttpExchange exchange) throws IOException {
    if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      exchange.sendResponseHeaders(405, -1);
      return;
    }
    exchange.sendResponseHeaders(200, -1);
  }

  private static void writeJson(HttpExchange exchange, int status, String body) throws IOException {
    byte[] payload = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(status, payload.length);
    try (OutputStream outputStream = exchange.getResponseBody()) {
      outputStream.write(payload);
    }
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    JwtDecoder cucumberJwtDecoder() {
      return mock(JwtDecoder.class);
    }

    @Bean
    public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
      return MockMvcBuilders.webAppContextSetup(webApplicationContext)
          .apply(springSecurity())
          .build();
    }
  }
}
