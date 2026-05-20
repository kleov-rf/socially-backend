package com.socially.app.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.socially.app.cucumber.CucumberDonationContext;
import com.socially.app.cucumber.CucumberOAuthJwt;
import com.socially.app.cucumber.CucumberSpringConfiguration;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import io.cucumber.spring.ScenarioScope;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@ScenarioScope
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DeleteDonationImageStepDefinitions {

  @Autowired private MockMvc mockMvc;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private CucumberDonationContext donationContext;

  @Autowired private DonationStepDefinitions donationStepDefinitions;

  @Autowired private CreateDonationImageStepDefinitions createDonationImageStepDefinitions;

  private String lastDeletedImageId;
  private String lastStorageObjectKey;

  @When("I delete the current donation image")
  public void iDeleteTheCurrentDonationImage() throws Exception {
    iDeleteDonationImage(
        createDonationImageStepDefinitions.getLastImageId(), cucumberDonorJwt(), true);
  }

  @When("I delete the current donation image without authentication")
  public void iDeleteTheCurrentDonationImageWithoutAuthentication() throws Exception {
    iDeleteDonationImage(createDonationImageStepDefinitions.getLastImageId(), null, true);
  }

  @When("I delete the current donation image as another user")
  public void iDeleteTheCurrentDonationImageAsAnotherUser() throws Exception {
    iDeleteDonationImage(
        createDonationImageStepDefinitions.getLastImageId(), cucumberOtherUserJwt(), true);
  }

  @When("I delete a donation image with unknown id")
  public void iDeleteADonationImageWithUnknownId() throws Exception {
    iDeleteDonationImage(UUID.randomUUID().toString(), cucumberDonorJwt(), false);
  }

  @And("the donation image should not be persisted for the current donation")
  public void theDonationImageShouldNotBePersistedForTheCurrentDonation() {
    Integer rowCount =
        jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM donation_images
            WHERE donation_id = ?::uuid AND id = ?::uuid
            """,
            Integer.class,
            donationContext.getCurrentDonationId(),
            lastDeletedImageId);

    assertThat(rowCount).isZero();
  }

  @And("the donation image object should not exist in S3")
  public void theDonationImageObjectShouldNotExistInS3() {
    try (S3Client s3Client = CucumberSpringConfiguration.createTestS3Client()) {
      assertThrows(
          NoSuchKeyException.class,
          () ->
              s3Client.headObject(
                  HeadObjectRequest.builder()
                      .bucket(CucumberSpringConfiguration.MEDIA_BUCKET)
                      .key(lastStorageObjectKey)
                      .build()));
    }
  }

  private void iDeleteDonationImage(
      String imageId, RequestPostProcessor authentication, boolean captureStorageObjectKey)
      throws Exception {
    lastDeletedImageId = imageId;
    if (captureStorageObjectKey) {
      lastStorageObjectKey = loadStorageObjectKey(imageId);
    }

    var requestBuilder =
        delete("/api/donations/" + donationContext.getCurrentDonationId() + "/images/" + imageId);

    if (authentication != null) {
      requestBuilder = requestBuilder.with(authentication);
    }

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    donationStepDefinitions.captureMvcResult(result);
  }

  private String loadStorageObjectKey(String imageId) {
    return jdbcTemplate.queryForObject(
        """
        SELECT storage_object_key
        FROM donation_images
        WHERE donation_id = ?::uuid AND id = ?::uuid
        """,
        String.class,
        donationContext.getCurrentDonationId(),
        imageId);
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
