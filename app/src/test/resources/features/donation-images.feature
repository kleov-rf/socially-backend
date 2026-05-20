@donation @donation-image
Feature: Donation images
  As a donor
  I want to add images to my donations
  So that I can show what is being donated

  Scenario: Create donation image returns presigned upload and persists metadata
    Given I have a donation with random id, title "Winter Clothes Drive" and description "Collecting warm clothes"
    When I create the donation
    Then the response status should be 201
    When I add a donation image with file name "photo.jpg" content type "image/jpeg" size 1024 and primary true
    Then the response status should be 201
    And the donation image response should include image id upload url and media url
    And the donation image should be persisted for the current donation
    When I upload the donation image bytes to the presigned upload url
    Then the presigned upload should succeed

  Scenario: Creating a donation image without authentication is rejected
    Given I have a donation with random id, title "No Auth Image" and description "Should not add image"
    When I create the donation
    Then the response status should be 201
    When I add a donation image with file name "photo.jpg" content type "image/jpeg" size 1024 and primary true without authentication
    Then the response status should be 401

  Scenario: Creating a donation image for another user's donation is rejected
    Given I have a donation with random id, title "Protected Donation Image" and description "Only owner can add images"
    When I create the donation
    Then the response status should be 201
    When I add a donation image with file name "photo.jpg" content type "image/jpeg" size 1024 and primary true as another user
    Then the response status should be 403
