package com.socially.auth.kernel.infrastructure.left;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CognitoMinistackJwtConfigurationTest {

  private final CognitoMinistackJwtConfiguration sut = new CognitoMinistackJwtConfiguration();

  @ParameterizedTest
  @CsvSource({
    "https://ministack.local/, https://ministack.local",
    "https://ministack.local, https://ministack.local",
  })
  void normalizeResourceIssuerUri_should_return_issuer_uri_without_final_slash(
      String input, String expected) {
    String actual = CognitoMinistackJwtConfiguration.normalizeResourceIssuerUri(input);

    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource({
    "https://ministack.local/, https://ministack.local/.well-known/jwks.json",
    "https://ministack.local, https://ministack.local/.well-known/jwks.json",
  })
  void should_append_jwks_json_to_normalized_base_issuer_uri(
      String issuerUri, String expectedJwkSetUri) {
    String actual = CognitoMinistackJwtConfiguration.ministackJwkSetUri(issuerUri);

    assertEquals(expectedJwkSetUri, actual);
  }

  @Test
  void should_use_trimmed_cognito_region_and_user_pool_id_for_jwt_issuer_claim() {
    String untrimmedRegion = "  eu-west-1  ";
    String untrimmedUserPoolId = "  pool-456  ";

    String actual =
        CognitoMinistackJwtConfiguration.cognitoIdpIssuerClaim(
            untrimmedRegion, untrimmedUserPoolId);

    String expected = "https://cognito-idp.eu-west-1.amazonaws.com/pool-456";
    assertEquals(expected, actual);
  }
}
