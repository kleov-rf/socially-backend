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
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DonationStepDefinitions {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  private String id;
  private String title;
  private String description;
  private MvcResult mvcResult;

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
