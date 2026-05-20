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

  Scenario: Retrieve donation by id includes persisted image with media url
    Given I have a donation with random id, title "GET Images Donation" and description "Donation with image for GET"
    When I create the donation
    Then the response status should be 201
    When I add a donation image with file name "photo.jpg" content type "image/jpeg" size 1024 and primary true
    Then the response status should be 201
    And the donation image response should include image id upload url and media url
    And the donation image should be persisted for the current donation
    When I upload the donation image bytes to the presigned upload url
    Then the presigned upload should succeed
    When I retrieve the donation by id
    Then the response status should be 200
    And the donation response should include one image matching the created donation image

  Scenario: Delete donation image removes DB row and S3 object
    Given I have a donation with random id, title "Delete Image Donation" and description "Donation with image to delete"
    When I create the donation
    Then the response status should be 201
    When I add a donation image with file name "photo.jpg" content type "image/jpeg" size 1024 and primary true
    Then the response status should be 201
    And the donation image should be persisted for the current donation
    When I upload the donation image bytes to the presigned upload url
    Then the presigned upload should succeed
    When I delete the current donation image
    Then the response status should be 204
    And the donation image should not be persisted for the current donation
    And the donation image object should not exist in S3

  Scenario: Deleting a donation image without authentication is rejected
    Given I have a donation with random id, title "No Auth Delete Image" and description "Should not delete image"
    When I create the donation
    Then the response status should be 201
    When I add a donation image with file name "photo.jpg" content type "image/jpeg" size 1024 and primary true
    Then the response status should be 201
    When I delete the current donation image without authentication
    Then the response status should be 401

  Scenario: Deleting a donation image for another user's donation is rejected
    Given I have a donation with random id, title "Protected Delete Image" and description "Only owner can delete images"
    When I create the donation
    Then the response status should be 201
    When I add a donation image with file name "photo.jpg" content type "image/jpeg" size 1024 and primary true
    Then the response status should be 201
    When I delete the current donation image as another user
    Then the response status should be 403

  Scenario: Deleting an unknown donation image id is rejected
    Given I have a donation with random id, title "Unknown Image Delete" and description "Image id not on donation"
    When I create the donation
    Then the response status should be 201
    When I add a donation image with file name "photo.jpg" content type "image/jpeg" size 1024 and primary true
    Then the response status should be 201
    When I delete a donation image with unknown id
    Then the response status should be 404
