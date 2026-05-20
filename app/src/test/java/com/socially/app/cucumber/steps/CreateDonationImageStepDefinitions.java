package com.socially.app.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.socially.app.cucumber.CucumberDonationContext;
import com.socially.app.cucumber.CucumberOAuthJwt;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@ScenarioScope
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class CreateDonationImageStepDefinitions {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private CucumberDonationContext donationContext;

  @Autowired private DonationStepDefinitions donationStepDefinitions;

  private String lastImageId;
  private String lastUploadUrl;
  private String lastMediaUrl;
  private String lastContentType;
  private Long lastSizeBytes;
  private Boolean lastPrimary;
  private Integer lastPresignedUploadStatus;

  String getLastImageId() {
    return lastImageId;
  }

  @When(
      "I add a donation image with file name {string} content type {string} size {long} and primary {word}")
  public void iAddADonationImageWithFileNameContentTypeSizeAndPrimary(
      String fileName, String contentType, Long sizeBytes, String primaryWord) throws Exception {
    iAddDonationImage(
        fileName, contentType, sizeBytes, Boolean.valueOf(primaryWord), cucumberDonorJwt());
  }

  @When(
      "I add a donation image with file name {string} content type {string} size {long} and primary {word} without authentication")
  public void iAddADonationImageWithoutAuthentication(
      String fileName, String contentType, Long sizeBytes, String primaryWord) throws Exception {
    iAddDonationImage(fileName, contentType, sizeBytes, Boolean.valueOf(primaryWord), null);
  }

  @When(
      "I add a donation image with file name {string} content type {string} size {long} and primary {word} as another user")
  public void iAddADonationImageAsAnotherUser(
      String fileName, String contentType, Long sizeBytes, String primaryWord) throws Exception {
    iAddDonationImage(
        fileName, contentType, sizeBytes, Boolean.valueOf(primaryWord), cucumberOtherUserJwt());
  }

  @And("the donation image response should include image id upload url and media url")
  public void theDonationImageResponseShouldIncludeImageIdUploadUrlAndMediaUrl() throws Exception {
    JsonNode response = parseLastResponseBody();

    assertThat(response.get("imageId").asText()).isNotBlank();
    assertThat(response.get("uploadUrl").asText()).isNotBlank();
    assertThat(response.get("mediaUrl").asText()).isNotBlank();
    assertThat(response.get("contentType").asText()).isEqualTo(lastContentType);
    assertThat(response.get("sizeBytes").asLong()).isEqualTo(lastSizeBytes);
    assertThat(response.get("primary").asBoolean()).isEqualTo(lastPrimary);
  }

  @And("the donation image should be persisted for the current donation")
  public void theDonationImageShouldBePersistedForTheCurrentDonation() {
    Integer rowCount =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM donation_images
            WHERE donation_id = ?::uuid AND id = ?::uuid
            """,
            Integer.class,
            donationContext.getCurrentDonationId(),
            lastImageId);

    assertThat(rowCount).isEqualTo(1);
  }

  @When("I upload the donation image bytes to the presigned upload url")
  public void iUploadTheDonationImageBytesToThePresignedUploadUrl() throws Exception {
    byte[] payload = new byte[lastSizeBytes.intValue()];
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(lastUploadUrl))
            .PUT(HttpRequest.BodyPublishers.ofByteArray(payload))
            .header("Content-Type", lastContentType)
            .build();

    HttpResponse<Void> response =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());
    lastPresignedUploadStatus = response.statusCode();
  }

  @Then("the presigned upload should succeed")
  public void thePresignedUploadShouldSucceed() {
    assertThat(lastPresignedUploadStatus).isEqualTo(200);
  }

  @And("the donation response should include one image matching the created donation image")
  public void theDonationResponseShouldIncludeOneImageMatchingTheCreatedDonationImage()
      throws Exception {
    JsonNode response =
        objectMapper.readTree(
            donationStepDefinitions.getMvcResult().getResponse().getContentAsString());
    JsonNode images = response.get("images");

    assertThat(images).isNotNull();
    assertThat(images.isArray()).isTrue();
    assertThat(images).hasSize(1);

    JsonNode image = images.get(0);
    assertThat(image.get("imageId").asText()).isEqualTo(lastImageId);
    assertThat(image.get("mediaUrl").asText()).isEqualTo(lastMediaUrl);
    assertThat(image.get("contentType").asText()).isEqualTo(lastContentType);
    assertThat(image.get("sizeBytes").asLong()).isEqualTo(lastSizeBytes);
    assertThat(image.get("primary").asBoolean()).isEqualTo(lastPrimary);
  }

  private void iAddDonationImage(
      String fileName,
      String contentType,
      Long sizeBytes,
      Boolean primary,
      RequestPostProcessor authentication)
      throws Exception {
    lastContentType = contentType;
    lastSizeBytes = sizeBytes;
    lastPrimary = primary;

    ObjectNode requestBody = objectMapper.createObjectNode();
    requestBody.put("originalFileName", fileName);
    requestBody.put("contentType", contentType);
    requestBody.put("sizeBytes", sizeBytes);
    requestBody.put("primary", primary);

    var requestBuilder =
        post("/api/donations/" + donationContext.getCurrentDonationId() + "/images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody));

    if (authentication != null) {
      requestBuilder = requestBuilder.with(authentication);
    }

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    donationStepDefinitions.captureMvcResult(result);

    if (result.getResponse().getStatus() == 201) {
      JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
      lastImageId = response.get("imageId").asText();
      lastUploadUrl = response.get("uploadUrl").asText();
      lastMediaUrl = response.get("mediaUrl").asText();
    }
  }

  private JsonNode parseLastResponseBody() throws Exception {
    String responseBody = donationStepDefinitions.getMvcResult().getResponse().getContentAsString();
    return objectMapper.readTree(responseBody);
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
