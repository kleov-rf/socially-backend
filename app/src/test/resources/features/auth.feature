@auth
Feature: Google auth flow
  As a Socially user
  I want to complete Google login through backend auth endpoints
  So that the SPA receives token payload and refresh cookie

  Scenario: Initialize Google login and complete callback
    Given I initialize Google login
    And the login response should include OAuth state and PKCE cookies
    When I complete the Google callback with authorization code "phase1-code"
    Then the auth response status should be 200
    And the auth response should include token and user payload
    And the auth response should set refresh token cookie
    And the users table should contain 1 records
    When I refresh the auth session
    Then the auth response status should be 200
    And the auth response should include token and user payload
    And the auth response should set refresh token cookie
    And the users table should contain 1 records
