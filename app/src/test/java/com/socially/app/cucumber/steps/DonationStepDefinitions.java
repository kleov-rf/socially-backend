package com.socially.app.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.socially.donation.create.infrastructure.left.adapter.http.create.input.CreateDonationRequest;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DonationStepDefinitions {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JdbcTemplate jdbcTemplate;

  private String id;
  private String title;
  private String description;
  private MvcResult mvcResult;

  private String firstDonationId;
  private String secondDonationId;
  private String lastCursor;
  private Integer lastPageSize;
  private String lastOrder;
  private String lastQuery;

  @Before("@donation")
  public void resetScenarioState() {
    jdbcTemplate.execute("DELETE FROM donations");
    firstDonationId = null;
    secondDonationId = null;
    lastCursor = null;
    lastPageSize = null;
    lastOrder = null;
    lastQuery = null;
  }

  @Given("I have a donation with random id, title {string} and description {string}")
  public void iHaveADonationWithRandomIdTitleAndDescription(String title, String description) {
    this.id = UUID.randomUUID().toString();
    this.title = title;
    this.description = description;
  }

  @When("I create the donation")
  public void iCreateTheDonation() throws Exception {
    String requestBody =
        objectMapper.writeValueAsString(new CreateDonationRequest(id, title, description));

    mvcResult =
        mockMvc
            .perform(
                post("/api/donations").contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andReturn();
  }

  @When("I delete the donation by id")
  public void iDeleteTheDonationById() throws Exception {
    mvcResult = mockMvc.perform(delete("/api/donations/" + id)).andReturn();
  }

  @When("I partially update the donation title to {string}")
  public void iPartiallyUpdateTheDonationTitleTo(String updatedTitle) throws Exception {
    ObjectNode requestBody = objectMapper.createObjectNode();
    requestBody.put("title", updatedTitle);

    mvcResult =
        mockMvc
            .perform(
                patch("/api/donations/" + id)
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
    mvcResult = mockMvc.perform(get("/api/donations").param("order", order)).andReturn();
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
  }

  @And("I create {int} additional donations for pagination")
  public void iCreateAdditionalDonationsForPagination(int donationsCount) throws Exception {
    for (int index = 0; index < donationsCount; index++) {
      String generatedId = UUID.randomUUID().toString();
      String generatedTitle = "Pagination Donation " + index;
      String generatedDescription = "Pagination description " + index;
      String requestBody =
          objectMapper.writeValueAsString(
              new CreateDonationRequest(generatedId, generatedTitle, generatedDescription));

      MvcResult createResult =
          mockMvc
              .perform(
                  post("/api/donations")
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

    assertThat(items.findValuesAsText("id")).contains(expectedIds);
  }

  @And("the donations page should include only donation id of donation {int}")
  public void theDonationsPageShouldIncludeOnlyDonationIdOfDonation(int donationNumber)
      throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode root = objectMapper.readTree(responseBody);
    JsonNode items = root.path("items");
    assertThat(items.isArray()).isTrue();
    assertThat(items.size()).isEqualTo(1);
    assertThat(items.findValuesAsText("id")).containsExactly(expectedDonationId(donationNumber));
  }

  @And("the donation should have the expected id, title {string} and description {string}")
  public void theDonationShouldHaveTheExpectedIdTitleAndDescription(
      String expectedTitle, String expectedDescription) throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseBody);

    assertThat(jsonNode.get("id").asText()).isEqualTo(id);
    assertThat(jsonNode.get("title").asText()).isEqualTo(expectedTitle);
    assertThat(jsonNode.get("description").asText()).isEqualTo(expectedDescription);
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
}
