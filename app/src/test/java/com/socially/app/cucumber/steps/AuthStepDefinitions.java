package com.socially.app.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socially.app.cucumber.CucumberOAuthJwt;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.servlet.http.Cookie;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AuthStepDefinitions {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private JdbcTemplate jdbcTemplate;

  private MvcResult mvcResult;
  private String state;
  private Map<String, String> cookies;

  @Before("@auth")
  public void resetScenarioState() {
    jdbcTemplate.execute("DELETE FROM users");
    mvcResult = null;
    state = null;
    cookies = new HashMap<>();
  }

  @Given("I initialize Google login")
  public void iInitializeGoogleLogin() throws Exception {
    mvcResult = mockMvc.perform(get("/api/auth/login/google")).andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);
    String loginUrl = root.path("url").asText();
    state = queryParams(loginUrl).get("state");
    cookies = parseSetCookieHeaders(mvcResult);
  }

  @And("the login response should include OAuth state and PKCE cookies")
  public void theLoginResponseShouldIncludeOAuthStateAndPkceCookies() {
    assertThat(state).isNotBlank();
    assertThat(cookies).containsKeys("socially_oauth_state", "socially_oauth_pkce");
  }

  @When("I complete the Google callback with authorization code {string}")
  public void iCompleteTheGoogleCallbackWithAuthorizationCode(String code) throws Exception {
    Cookie[] cookieArray =
        cookies.entrySet().stream()
            .map(entry -> new Cookie(entry.getKey(), entry.getValue()))
            .toArray(Cookie[]::new);

    mvcResult =
        mockMvc
            .perform(
                get("/api/auth/callback/google")
                    .param("code", code)
                    .param("state", state)
                    .cookie(cookieArray))
            .andReturn();
    cookies.putAll(parseSetCookieHeaders(mvcResult));
  }

  @When("I refresh the auth session")
  public void iRefreshTheAuthSession() throws Exception {
    Cookie[] cookieArray =
        cookies.entrySet().stream()
            .map(entry -> new Cookie(entry.getKey(), entry.getValue()))
            .toArray(Cookie[]::new);

    mvcResult = mockMvc.perform(post("/api/auth/refresh").cookie(cookieArray)).andReturn();
    cookies.putAll(parseSetCookieHeaders(mvcResult));
  }

  @When("I request the current user profile")
  public void iRequestTheCurrentUserProfile() throws Exception {
    mvcResult =
        mockMvc.perform(get("/api/auth/me").with(CucumberOAuthJwt.postProcessor())).andReturn();
  }

  @Then("the auth response status should be {int}")
  public void theAuthResponseStatusShouldBe(int status) {
    assertThat(mvcResult.getResponse().getStatus()).isEqualTo(status);
  }

  @And("the auth response should include token and user payload")
  public void theAuthResponseShouldIncludeTokenAndUserPayload() throws Exception {
    JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    assertThat(root.path("accessToken").asText()).isEqualTo("phase1-access-token");
    assertThat(root.path("tokenType").asText()).isEqualTo("Bearer");
    assertThat(root.path("expiresIn").asLong()).isEqualTo(3600L);
    assertThat(root.path("user").path("id").asText())
        .matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    assertThat(root.path("user").path("email").asText()).isEqualTo("auth.user@example.com");
    assertThat(root.path("user").path("name").asText()).isEqualTo("Auth User");
  }

  @And("the auth response should set refresh token cookie")
  public void theAuthResponseShouldSetRefreshTokenCookie() {
    String setCookieHeaders = String.join(",", mvcResult.getResponse().getHeaders("Set-Cookie"));
    assertThat(setCookieHeaders).contains("socially_refresh_token=");
  }

  @And("the users table should contain {int} records")
  public void theUsersTableShouldContainRecords(int expectedCount) {
    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
    assertThat(count).isEqualTo(expectedCount);
  }

  @And("the auth profile response should include donor for the OAuth user")
  public void theAuthProfileResponseShouldIncludeDonorForTheOauthUser() throws Exception {
    JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    assertThat(root.path("user").path("email").asText()).isEqualTo("auth.user@example.com");
    assertThat(root.path("user").path("id").asText())
        .matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    JsonNode profiles = root.path("profiles");
    assertThat(profiles.path("organization").isNull()).isTrue();

    JsonNode donor = profiles.path("donor");
    assertThat(donor.isMissingNode()).isFalse();
    assertThat(donor.isNull()).isFalse();
    assertThat(donor.path("email").asText()).isEqualTo("auth.user@example.com");
    assertThat(donor.path("givenName").asText()).isEqualTo("Auth");
    assertThat(donor.path("familyName").asText()).isEqualTo("User");
    assertThat(donor.path("id").asText())
        .matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
  }

  private static Map<String, String> queryParams(String url) {
    if (url == null || url.isBlank()) {
      return Map.of();
    }
    String query = URI.create(url).getRawQuery();
    if (query == null || query.isEmpty()) {
      return Map.of();
    }
    return Arrays.stream(query.split("&"))
        .map(value -> value.split("=", 2))
        .collect(
            Collectors.toMap(
                parts -> decode(parts[0]), parts -> parts.length > 1 ? decode(parts[1]) : ""));
  }

  private static Map<String, String> parseSetCookieHeaders(MvcResult result) {
    Map<String, String> values = new HashMap<>();
    for (String header : result.getResponse().getHeaders("Set-Cookie")) {
      String nameValue = header.split(";", 2)[0];
      String[] pair = nameValue.split("=", 2);
      if (pair.length == 2) {
        values.put(pair[0], pair[1]);
      }
    }
    return values;
  }

  private static String decode(String value) {
    return java.net.URLDecoder.decode(value, java.nio.charset.StandardCharsets.UTF_8);
  }
}
