Feature: Donation management
  As a user of the Socially platform
  I want to create and retrieve donations
  So that I can manage charitable contributions

  Scenario: Create a donation and retrieve it by ID
    Given I have a donation with random id, title "Winter Clothes Drive" and description "Collecting warm clothes for homeless shelters"
    When I create the donation
    Then the response status should be 201
    When I retrieve the donation by id
    Then the response status should be 200
    And the donation should have the expected id, title "Winter Clothes Drive" and description "Collecting warm clothes for homeless shelters"
