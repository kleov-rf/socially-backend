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

  Scenario: List donations returns both created donations
    Given I have a donation with random id, title "First List Donation" and description "First list description"
    When I create the donation
    Then the response status should be 201
    And I record this donation as donation 1
    Given I have a donation with random id, title "Second List Donation" and description "Second list description"
    When I create the donation
    Then the response status should be 201
    And I record this donation as donation 2
    When I retrieve all donations
    Then the response status should be 200
    And the donations page should include both recorded donation ids
    And the response should include pagination metadata

  Scenario: Retrieve donations using cursor pagination
    Given I have a donation with random id, title "New Donation" and description "Cursor description one"
    When I create the donation
    Then the response status should be 201
    And I record this donation as donation 1
    Given I have a donation with random id, title "Newer Donation" and description "Cursor description two"
    When I create the donation
    Then the response status should be 201
    And I record this donation as donation 2
    And I create 4 additional donations for pagination
    When I retrieve donations with page size 5
    Then the response status should be 200
    And the current page should include donation id of donation 2
    And the response should include pagination metadata
    And the pagination should indicate a next page
    When I retrieve next donations page using the returned cursor
    Then the response status should be 200
    And the response should include pagination metadata
    And the next page should include donation id of donation 1
    When I retrieve previous donations page using the returned cursor
    Then the response status should be 200
    And the response should include pagination metadata
    And the current page should include donation id of donation 2

  Scenario: Retrieve donations with oldest first order
    Given I have a donation with random id, title "Oldest donation" and description "Oldest order item"
    When I create the donation
    Then the response status should be 201
    And I record this donation as donation 1
    Given I have a donation with random id, title "Newest donation" and description "Newest order item"
    When I create the donation
    Then the response status should be 201
    And I record this donation as donation 2
    When I retrieve all donations with order "oldest_first"
    Then the response status should be 200
    And the first donation in the current page should be donation 1

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
    And the donation last updated time should be after the created time
