package com.socially.app.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.socially.app.cucumber.CucumberDonationContext;
import com.socially.app.cucumber.CucumberOAuthJwt;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationLocationRequest;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@ScenarioScope
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DonationStepDefinitions {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private CucumberDonationContext donationContext;

  private String id;
  private String title;
  private String description;
  private CreateDonationLocationRequest expectedLocation = DEFAULT_LOCATION;
  private String lastCreateRequestBody;
  private MvcResult mvcResult;

  private String firstDonationId;
  private String secondDonationId;
  private String lastCursor;
  private Integer lastPageSize;
  private String lastOrder;
  private String lastQuery;
  private Double lastLatitude;
  private Double lastLongitude;

  private static final String CUCUMBER_DONOR_USER_ID = "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeee0001";
  private static final String CUCUMBER_DONOR_ID = "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeee0002";
  private static final String CUCUMBER_OTHER_USER_ID = "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeee0003";
  private static final String CUCUMBER_OTHER_DONOR_ID = "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeee0004";
  private static final String CUCUMBER_FEDERATED_IDENTITY_ID_PREFIX =
      "aaaaaaaa-bbbb-4ccc-8ddd-eeeeeeee001";
  private static final CreateDonationLocationRequest DEFAULT_LOCATION =
      new CreateDonationLocationRequest("Calle Mayor 1, Madrid", 40.4168, -3.7038);

  @Before("@donation and not @auth")
  public void resetDonationScenarioState() {
    resetDonationData();
    seedCucumberDonorUsers();
    firstDonationId = null;
    secondDonationId = null;
    lastCursor = null;
    lastPageSize = null;
    lastOrder = null;
    lastQuery = null;
    lastLatitude = null;
    lastLongitude = null;
    expectedLocation = DEFAULT_LOCATION;
    lastCreateRequestBody = null;
  }

  @Before("@donation and @auth")
  public void resetAuthDonationScenarioState() {
    jdbcTemplate.execute("DELETE FROM donation_images");
    jdbcTemplate.execute("DELETE FROM donations");
    firstDonationId = null;
    secondDonationId = null;
    lastCursor = null;
    lastPageSize = null;
    lastOrder = null;
    lastQuery = null;
    lastLatitude = null;
    lastLongitude = null;
    expectedLocation = DEFAULT_LOCATION;
    lastCreateRequestBody = null;
  }

  private void resetDonationData() {
    jdbcTemplate.execute("DELETE FROM donation_images");
    jdbcTemplate.execute("DELETE FROM donations");
    jdbcTemplate.execute("DELETE FROM donors");
    jdbcTemplate.execute("DELETE FROM federated_identities");
    jdbcTemplate.execute("DELETE FROM users");
  }

  @Given("I have a donation with random id, title {string} and description {string}")
  public void iHaveADonationWithRandomIdTitleAndDescription(String title, String description) {
    this.id = UUID.randomUUID().toString();
    donationContext.setCurrentDonationId(this.id);
    this.title = title;
    this.description = description;
    this.expectedLocation = DEFAULT_LOCATION;
  }

  @Given("the donation location is address {string} latitude {double} and longitude {double}")
  public void theDonationLocationIs(String address, double latitude, double longitude) {
    this.expectedLocation = new CreateDonationLocationRequest(address, latitude, longitude);
  }

  @When("I create the donation")
  public void iCreateTheDonation() throws Exception {
    lastCreateRequestBody =
        objectMapper.writeValueAsString(createDonationRequest(id, title, description));

    mvcResult =
        mockMvc
            .perform(
                post("/api/donations")
                    .with(cucumberDonorJwt())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(lastCreateRequestBody))
            .andReturn();
  }

  @When("I create the donation as the logged-in OAuth user")
  public void iCreateTheDonationAsTheLoggedInOauthUser() throws Exception {
    lastCreateRequestBody =
        objectMapper.writeValueAsString(createDonationRequest(id, title, description));

    mvcResult =
        mockMvc
            .perform(
                post("/api/donations")
                    .with(CucumberOAuthJwt.postProcessor())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(lastCreateRequestBody))
            .andReturn();
  }

  @When("I create the donation without authentication")
  public void iCreateTheDonationWithoutAuthentication() throws Exception {
    String requestBody =
        objectMapper.writeValueAsString(createDonationRequest(id, title, description));

    mvcResult =
        mockMvc
            .perform(
                post("/api/donations").contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andReturn();
  }

  @When("I delete the donation by id")
  public void iDeleteTheDonationById() throws Exception {
    mvcResult =
        mockMvc.perform(delete("/api/donations/" + id).with(cucumberDonorJwt())).andReturn();
  }

  @When("I delete the donation by id without authentication")
  public void iDeleteTheDonationByIdWithoutAuthentication() throws Exception {
    mvcResult = mockMvc.perform(delete("/api/donations/" + id)).andReturn();
  }

  @When("I delete the donation by id as another user")
  public void iDeleteTheDonationByIdAsAnotherUser() throws Exception {
    mvcResult =
        mockMvc.perform(delete("/api/donations/" + id).with(cucumberOtherUserJwt())).andReturn();
  }

  @When("I partially update the donation title to {string}")
  public void iPartiallyUpdateTheDonationTitleTo(String updatedTitle) throws Exception {
    ObjectNode requestBody = objectMapper.createObjectNode();
    requestBody.put("title", updatedTitle);

    mvcResult =
        mockMvc
            .perform(
                patch("/api/donations/" + id)
                    .with(cucumberDonorJwt())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
            .andReturn();
  }

  @When(
      "I partially update the donation location to address {string} latitude {double} and longitude {double}")
  public void iPartiallyUpdateTheDonationLocationTo(
      String address, double latitude, double longitude) throws Exception {
    ObjectNode requestBody = objectMapper.createObjectNode();
    ObjectNode locationNode = requestBody.putObject("location");
    locationNode.put("address", address);
    locationNode.put("latitude", latitude);
    locationNode.put("longitude", longitude);

    mvcResult =
        mockMvc
            .perform(
                patch("/api/donations/" + id)
                    .with(cucumberDonorJwt())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
            .andReturn();
  }

  @When("I partially update the donation by id without authentication")
  public void iPartiallyUpdateTheDonationByIdWithoutAuthentication() throws Exception {
    ObjectNode requestBody = objectMapper.createObjectNode();
    requestBody.put("title", "Unauthorized update");

    mvcResult =
        mockMvc
            .perform(
                patch("/api/donations/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
            .andReturn();
  }

  @When("I partially update the donation by id as another user")
  public void iPartiallyUpdateTheDonationByIdAsAnotherUser() throws Exception {
    ObjectNode requestBody = objectMapper.createObjectNode();
    requestBody.put("title", "Forbidden update");

    mvcResult =
        mockMvc
            .perform(
                patch("/api/donations/" + id)
                    .with(cucumberOtherUserJwt())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody)))
            .andReturn();
  }

  @Then("the response status should be {int}")
  public void theResponseStatusShouldBe(int expectedStatus) {
    assertThat(mvcResult.getResponse().getStatus()).isEqualTo(expectedStatus);
  }

  @When("I retrieve the donation by id")
  public void iRetrieveTheDonationById() throws Exception {
    mvcResult = mockMvc.perform(get("/api/donations/" + id)).andReturn();
  }

  @When("I retrieve all donations")
  public void iRetrieveAllDonations() throws Exception {
    lastPageSize = null;
    lastOrder = null;
    lastQuery = null;
    mvcResult = mockMvc.perform(get("/api/donations")).andReturn();
  }

  @When("I retrieve all donations with order {string}")
  public void iRetrieveAllDonationsWithOrder(String order) throws Exception {
    lastPageSize = null;
    lastOrder = order;
    lastQuery = null;
    lastLatitude = null;
    lastLongitude = null;
    mvcResult = mockMvc.perform(get("/api/donations").param("order", order)).andReturn();
  }

  @When(
      "I retrieve all donations with order {string} from latitude {double} and longitude {double}")
  public void iRetrieveAllDonationsWithOrderFromCoordinates(
      String order, double latitude, double longitude) throws Exception {
    lastPageSize = null;
    lastOrder = order;
    lastQuery = null;
    lastLatitude = latitude;
    lastLongitude = longitude;
    mvcResult =
        mockMvc
            .perform(
                get("/api/donations")
                    .param("order", order)
                    .param("latitude", String.valueOf(latitude))
                    .param("longitude", String.valueOf(longitude)))
            .andReturn();
  }

  @When("I retrieve all donations with query {string}")
  public void iRetrieveAllDonationsWithQuery(String query) throws Exception {
    lastPageSize = null;
    lastOrder = null;
    lastQuery = query;
    mvcResult = mockMvc.perform(get("/api/donations").param("query", query)).andReturn();
  }

  @When("I retrieve donations with page size {int}")
  public void iRetrieveDonationsWithPageSize(int size) throws Exception {
    lastPageSize = size;
    lastOrder = null;
    lastQuery = null;
    mvcResult =
        mockMvc.perform(get("/api/donations").param("size", String.valueOf(size))).andReturn();
  }

  @When("I retrieve next donations page using the returned cursor")
  public void iRetrieveNextDonationsPageUsingTheReturnedCursor() throws Exception {
    retrieveDonationsPageUsingReturnedCursor("nextCursor");
  }

  @When("I retrieve previous donations page using the returned cursor")
  public void iRetrievePreviousDonationsPageUsingTheReturnedCursor() throws Exception {
    retrieveDonationsPageUsingReturnedCursor("previousCursor");
  }

  @And("I record this donation as donation {int}")
  public void iRecordThisDonationAsDonation(int donationNumber) {
    if (donationNumber == 1) {
      firstDonationId = id;
    } else if (donationNumber == 2) {
      secondDonationId = id;
    } else {
      throw new IllegalArgumentException(
          "Only donation 1 or 2 is supported, got: " + donationNumber);
    }
  }

  @And("I select donation {int} as current donation id")
  public void iSelectDonationAsCurrentDonationId(int donationNumber) {
    id = expectedDonationId(donationNumber);
    donationContext.setCurrentDonationId(id);
  }

  public void captureMvcResult(MvcResult result) {
    this.mvcResult = result;
  }

  public MvcResult getMvcResult() {
    return mvcResult;
  }

  @And("I create {int} additional donations for pagination")
  public void iCreateAdditionalDonationsForPagination(int donationsCount) throws Exception {
    for (int index = 0; index < donationsCount; index++) {
      String generatedId = UUID.randomUUID().toString();
      String generatedTitle = "Pagination Donation " + index;
      String generatedDescription = "Pagination description " + index;
      String requestBody =
          objectMapper.writeValueAsString(
              createDonationRequest(generatedId, generatedTitle, generatedDescription));

      MvcResult createResult =
          mockMvc
              .perform(
                  post("/api/donations")
                      .with(cucumberDonorJwt())
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(requestBody))
              .andReturn();

      assertThat(createResult.getResponse().getStatus()).isEqualTo(201);
    }
  }

  @And("the donations page should include both recorded donation ids")
  public void theDonationsPageShouldIncludeBothRecordedDonationIds() throws Exception {
    assertMvcItemsContainDonationIds(firstDonationId, secondDonationId);
  }

  @And("the response should include pagination metadata")
  public void theResponseShouldIncludePaginationMetadata() throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);
    JsonNode page = root.path("page");

    assertThat(root.has("items")).isTrue();
    assertThat(page.isMissingNode()).isFalse();
    assertThat(page.has("nextCursor")).isTrue();
    assertThat(page.has("previousCursor")).isTrue();
    assertThat(page.has("hasNext")).isTrue();
    assertThat(page.has("hasPrevious")).isTrue();
    assertThat(page.has("size")).isTrue();
    assertThat(page.has("totalCount")).isTrue();
  }

  @And("the pagination should indicate a next page")
  public void thePaginationShouldIndicateANextPage() throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);
    JsonNode page = root.path("page");

    assertThat(page.path("hasNext").asBoolean()).isTrue();
    assertThat(page.path("nextCursor").asText()).isNotBlank();
  }

  @And("the current page should include donation id of donation {int}")
  @And("the next page should include donation id of donation {int}")
  public void theCurrentPageShouldIncludeDonationIdOfDonation(int donationNumber) throws Exception {
    assertMvcItemsContainDonationIds(expectedDonationId(donationNumber));
  }

  private String expectedDonationId(int donationNumber) {
    return donationNumber == 1 ? firstDonationId : secondDonationId;
  }

  private void retrieveDonationsPageUsingReturnedCursor(String cursorFieldName) throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);
    lastCursor = root.path("page").path(cursorFieldName).asText();
    assertThat(lastCursor).isNotBlank();

    var request = get("/api/donations").param("cursor", lastCursor);
    if (lastPageSize != null) {
      request = request.param("size", String.valueOf(lastPageSize));
    }
    if (lastOrder != null) {
      request = request.param("order", lastOrder);
    }
    if (lastQuery != null) {
      request = request.param("query", lastQuery);
    }
    if (lastLatitude != null && lastLongitude != null) {
      request = request.param("latitude", String.valueOf(lastLatitude));
      request = request.param("longitude", String.valueOf(lastLongitude));
    }

    mvcResult = mockMvc.perform(request).andReturn();
  }

  @And("the first donation in the current page should be donation {int}")
  public void theFirstDonationInTheCurrentPageShouldBeDonation(int donationNumber)
      throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);
    JsonNode items = root.path("items");
    assertThat(items.isArray()).isTrue();
    assertThat(items.size()).isGreaterThan(0);
    assertThat(items.get(0).path("id").asText()).isEqualTo(expectedDonationId(donationNumber));
  }

  private void assertMvcItemsContainDonationIds(String... expectedIds) throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);
    JsonNode items = root.path("items");
    assertThat(items.isArray()).isTrue();

    assertThat(donationIdsFromListItems(items)).contains(expectedIds);
  }

  @And("the donations page should include only donation id of donation {int}")
  public void theDonationsPageShouldIncludeOnlyDonationIdOfDonation(int donationNumber)
      throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);
    JsonNode items = root.path("items");
    assertThat(items.isArray()).isTrue();
    assertThat(items.size()).isEqualTo(1);
    assertThat(donationIdsFromListItems(items)).containsExactly(expectedDonationId(donationNumber));
  }

  private static List<String> donationIdsFromListItems(JsonNode items) {
    List<String> donationIds = new ArrayList<>();
    for (JsonNode item : items) {
      donationIds.add(item.path("id").asText());
    }
    return donationIds;
  }

  @And("the donation should have the expected id, title {string} and description {string}")
  public void theDonationShouldHaveTheExpectedIdTitleAndDescription(
      String expectedTitle, String expectedDescription) throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseBody);

    assertThat(jsonNode.get("id").asText()).isEqualTo(id);
    assertThat(jsonNode.get("title").asText()).isEqualTo(expectedTitle);
    assertThat(jsonNode.get("description").asText()).isEqualTo(expectedDescription);
    JsonNode locationNode = jsonNode.get("location");
    assertThat(locationNode).isNotNull();
    assertThat(locationNode.isObject()).isTrue();
    assertThat(locationNode.get("address").asText()).isEqualTo(expectedLocation.address());
    assertThat(locationNode.get("latitude").asDouble()).isEqualTo(expectedLocation.latitude());
    assertThat(locationNode.get("longitude").asDouble()).isEqualTo(expectedLocation.longitude());
    JsonNode donorNode = jsonNode.get("donor");
    assertThat(donorNode).isNotNull();
    assertThat(donorNode.isObject()).isTrue();
    assertThat(donorNode.get("email").asText()).isEqualTo("donor@example.com");
    assertThat(donorNode.get("givenName").asText()).isEqualTo("Donor");
    assertThat(donorNode.get("familyName").asText()).isEqualTo("User");
    assertThat(donorNode.get("id").asText())
        .matches(Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
    assertThatCode(() -> UUID.fromString(donorNode.get("id").asText())).doesNotThrowAnyException();
    assertThat(jsonNode.has("createdAt")).isTrue();
    assertThat(jsonNode.get("createdAt").asText()).isNotBlank();
    assertThatCode(() -> Instant.parse(jsonNode.get("createdAt").asText()))
        .doesNotThrowAnyException();
    assertThat(jsonNode.has("lastUpdatedAt")).isTrue();
    assertThat(jsonNode.get("lastUpdatedAt").asText()).isNotBlank();
    assertThatCode(() -> Instant.parse(jsonNode.get("lastUpdatedAt").asText()))
        .doesNotThrowAnyException();
  }

  @And("the donation last updated time should be after the created time")
  public void theDonationLastUpdatedTimeShouldBeAfterTheCreatedTime() throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseBody);

    Instant createdAt = Instant.parse(jsonNode.get("createdAt").asText());
    Instant lastUpdatedAt = Instant.parse(jsonNode.get("lastUpdatedAt").asText());

    assertThat(lastUpdatedAt).isAfter(createdAt);
  }

  private void seedCucumberDonorUsers() {
    String issuer = CucumberOAuthJwt.TEST_ISSUER;
    Timestamp createdAt = Timestamp.from(Instant.parse("2024-01-01T00:00:00Z"));

    jdbcTemplate.update(
        """
        INSERT INTO users (id, email, given_name, family_name, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        UUID.fromString(CUCUMBER_DONOR_USER_ID),
        "donor@example.com",
        "Donor",
        "User",
        createdAt);
    jdbcTemplate.update(
        """
        INSERT INTO federated_identities (id, user_id, issuer, subject, email, created_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        UUID.fromString(CUCUMBER_FEDERATED_IDENTITY_ID_PREFIX + "1"),
        UUID.fromString(CUCUMBER_DONOR_USER_ID),
        issuer,
        "cucumber-donor-sub",
        "donor@example.com",
        createdAt);
    jdbcTemplate.update(
        """
        INSERT INTO donors (id, user_id, email, given_name, family_name, created_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        UUID.fromString(CUCUMBER_DONOR_ID),
        UUID.fromString(CUCUMBER_DONOR_USER_ID),
        "donor@example.com",
        "Donor",
        "User",
        createdAt);

    jdbcTemplate.update(
        """
        INSERT INTO users (id, email, given_name, family_name, created_at)
        VALUES (?, ?, ?, ?, ?)
        """,
        UUID.fromString(CUCUMBER_OTHER_USER_ID),
        "other@example.com",
        "Other",
        "User",
        createdAt);
    jdbcTemplate.update(
        """
        INSERT INTO federated_identities (id, user_id, issuer, subject, email, created_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        UUID.fromString(CUCUMBER_FEDERATED_IDENTITY_ID_PREFIX + "2"),
        UUID.fromString(CUCUMBER_OTHER_USER_ID),
        issuer,
        "cucumber-other-user-sub",
        "other@example.com",
        createdAt);
    jdbcTemplate.update(
        """
        INSERT INTO donors (id, user_id, email, given_name, family_name, created_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        UUID.fromString(CUCUMBER_OTHER_DONOR_ID),
        UUID.fromString(CUCUMBER_OTHER_USER_ID),
        "other@example.com",
        "Other",
        "User",
        createdAt);
  }

  private CreateDonationRequest createDonationRequest(String id, String title, String description) {
    return new CreateDonationRequest(id, title, description, expectedLocation);
  }

  private static RequestPostProcessor cucumberDonorJwt() {
    return jwt()
        .jwt(
            builder ->
                builder
                    .issuer(CucumberOAuthJwt.TEST_ISSUER)
                    .subject("cucumber-donor-sub")
                    .claim("email", "donor@example.com")
                    .claim("given_name", "Donor")
                    .claim("family_name", "User"));
  }

  private static RequestPostProcessor cucumberOtherUserJwt() {
    return jwt()
        .jwt(
            builder ->
                builder
                    .issuer(CucumberOAuthJwt.TEST_ISSUER)
                    .subject("cucumber-other-user-sub")
                    .claim("email", "other@example.com")
                    .claim("given_name", "Other")
                    .claim("family_name", "User"));
  }
}
