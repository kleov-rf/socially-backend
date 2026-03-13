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

  Scenario: Create a donation, delete it, and fail to retrieve it afterwards
    Given I have a donation with random id, title "School Supplies Fund" and description "Raising money for notebooks and backpacks"
    When I create the donation
    Then the response status should be 201
    When I delete the donation by id
    Then the response status should be 204
    When I retrieve the donation by id
    Then the response status should be 404

  Scenario: Create a donation, patch only title, and keep description unchanged
    Given I have a donation with random id, title "Neighborhood Library" and description "Books and shelves for local students"
    When I create the donation
    Then the response status should be 201
    When I partially update the donation title to "Neighborhood Library Expansion"
    Then the response status should be 204
    When I retrieve the donation by id
    Then the response status should be 200
    And the donation should have the expected id, title "Neighborhood Library Expansion" and description "Books and shelves for local students"
